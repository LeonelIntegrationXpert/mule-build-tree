/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  org.apache.commons.lang.StringUtils
 *  org.apache.log4j.Logger
 */
package org.mule.devkit.3.9.0.internal.lic.model;

import com.google.common.base.Optional;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mule.devkit.3.9.0.internal.lic.SecurityUtils;
import org.mule.devkit.3.9.0.internal.lic.exception.InvalidKeyException;
import org.mule.devkit.3.9.0.internal.lic.exception.InvalidLicenseException;
import org.mule.devkit.3.9.0.internal.lic.model.Entitlement;
import org.mule.devkit.3.9.0.internal.lic.model.LicenseProviderData;
import org.mule.devkit.3.9.0.internal.lic.model.Version;
import org.mule.devkit.3.9.0.internal.lic.model.ZippedBundle;

public class CustomLicense {
    public static final String FEATURE_KEY = "feature.name";
    public static final String VERSIONS_KEY = "valid.versions";
    public static final String VENDOR_NAME_KEY = "vendor.name";
    public static final String CREATION_DATE_KEY = "creation.date";
    public static final String EXPIRATION_DATE_KEY = "expiration.date";
    private static final Logger logger = Logger.getLogger(CustomLicense.class);
    private static final Pattern versionRangePattern = Pattern.compile("(\\[|\\()([^,]*),(.*)(\\]|\\))");
    private final String featureId;
    private final String licFile;
    private final String infoFile;
    private final String signatureFile;
    private final Properties data;

    public CustomLicense(String featureId, String licName, LicenseProviderData licenseProviderData) throws InvalidKeyException {
        if (StringUtils.isBlank((String)featureId) || StringUtils.isBlank((String)licName)) {
            throw new IllegalArgumentException("Provider and ID and license name cannot be blank");
        }
        this.featureId = featureId;
        this.licFile = licName.concat(".lic");
        this.signatureFile = licName.concat(".sig");
        this.infoFile = licName.concat(".info");
        try {
            ZippedBundle licBundle = this.loadLicBundle(this.licFile);
            if (!this.signatureIsValid(licBundle, licenseProviderData.getKey())) {
                throw new InvalidLicenseException("[" + this.signatureFile + "] signature is not valid for license " + this.infoFile);
            }
            this.data = this.loadMetaData(licBundle);
        }
        catch (InvalidLicenseException e) {
            throw new InvalidLicenseException(String.format("Required License %s was not found or is not a valid license. Please contact your license provider %s at %s. %s", this.licFile, licenseProviderData.getName(), licenseProviderData.getEmail(), licenseProviderData.getContactMessage()), e);
        }
    }

    public Optional<String> getProperty(String propertyName) {
        return Optional.fromNullable((Object)this.data.getProperty(propertyName));
    }

    public boolean isValid(Entitlement entitlement) {
        return this.hasValidFeature() && this.hasValidDate() && this.hasValidVersion(entitlement.version());
    }

    public boolean hasValidVersion(String version) {
        Matcher rangeMatcher;
        Version current = new Version(version);
        String validVersions = this.data.getProperty(VERSIONS_KEY);
        if (!StringUtils.isBlank((String)validVersions) && (rangeMatcher = versionRangePattern.matcher(validVersions)).matches()) {
            boolean minIncluded = StringUtils.equals((String)rangeMatcher.group(1), (String)"[");
            String minVersion = rangeMatcher.group(2);
            String maxVersion = rangeMatcher.group(3);
            boolean maxIncluded = StringUtils.equals((String)rangeMatcher.group(4), (String)"]");
            if (!StringUtils.isBlank((String)minVersion) && !new Version(minVersion).isLowerThan(current, minIncluded) || !StringUtils.isBlank((String)maxVersion) && !new Version(maxVersion).isGraterThan(current, maxIncluded)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasValidFeature() {
        return StringUtils.equals((String)this.data.getProperty(FEATURE_KEY), (String)this.featureId);
    }

    public boolean hasValidDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        Date today = new Date();
        String creation = this.data.getProperty(CREATION_DATE_KEY);
        String expiration = this.data.getProperty(EXPIRATION_DATE_KEY);
        if (StringUtils.isBlank((String)creation)) {
            throw new InvalidLicenseException("Invalid data found inside license info. Missing creation Date");
        }
        try {
            return !today.before(dateFormat.parse(creation)) && (StringUtils.isBlank((String)expiration) || !today.after(dateFormat.parse(expiration)));
        }
        catch (ParseException e) {
            throw new InvalidLicenseException("Invalid date found inside license info. Failed to parse creation or expiration date", e);
        }
    }

    private boolean signatureIsValid(ZippedBundle lic, PublicKey vendorKey) throws InvalidKeyException {
        Optional<byte[]> signature = lic.get(this.signatureFile);
        if (!signature.isPresent()) {
            logger.error((Object)("No signature named " + this.signatureFile + " found in bundle " + this.licFile));
            throw new InvalidLicenseException("Failed to verify signature " + this.signatureFile + ". No signature named " + this.signatureFile + " found in bundle " + this.licFile);
        }
        Optional<byte[]> metadata = lic.get(this.infoFile);
        if (!metadata.isPresent()) {
            logger.error((Object)("No info file named " + this.infoFile + " found in bundle " + this.licFile));
            throw new InvalidLicenseException("Failed to verify license metadata. No info file named " + this.infoFile + " found in bundle " + this.licFile);
        }
        try {
            return SecurityUtils.verify((byte[])signature.get(), (byte[])metadata.get(), vendorKey);
        }
        catch (Exception e) {
            logger.error((Object)e.getMessage());
            throw new InvalidLicenseException("An error occurred wile trying to verify signature [" + this.signatureFile + "] for file " + this.infoFile);
        }
    }

    private Properties loadMetaData(ZippedBundle licBundle) throws InvalidKeyException {
        Optional<byte[]> info = licBundle.get(this.infoFile);
        if (!info.isPresent()) {
            logger.error((Object)("No metadata file named " + this.infoFile + " found in bundle " + this.infoFile));
            throw new InvalidLicenseException("Failed to verify license metadata. No info file named " + this.infoFile + " found in bundle " + this.licFile);
        }
        try {
            Properties metadata = new Properties();
            metadata.load(new ByteArrayInputStream((byte[])info.get()));
            return metadata;
        }
        catch (Exception e) {
            logger.error((Object)e.getMessage());
            throw new InvalidLicenseException("Failed to load license properties from " + this.infoFile + " found in license " + licBundle);
        }
    }

    private ZippedBundle loadLicBundle(String licFile) {
        InputStream bundleIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(licFile);
        if (bundleIS == null) {
            logger.error((Object)("License file with name [" + licFile + "] not found as resource"));
            throw new InvalidLicenseException("License with name [" + licFile + "] not found as resource. License was not provided or its name is not the expected");
        }
        return new ZippedBundle(bundleIS);
    }
}
