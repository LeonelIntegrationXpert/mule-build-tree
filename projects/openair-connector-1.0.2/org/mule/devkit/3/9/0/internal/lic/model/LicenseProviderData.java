/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  org.apache.commons.lang.StringUtils
 */
package org.mule.devkit.3.9.0.internal.lic.model;

import com.google.common.base.Optional;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.PublicKey;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.mule.devkit.3.9.0.internal.lic.SecurityUtils;
import org.mule.devkit.3.9.0.internal.lic.exception.InvalidKeyException;
import org.mule.devkit.3.9.0.internal.lic.model.ZippedBundle;

public class LicenseProviderData {
    public static final String CONTACT_EMAIL_KEY = "contact.email";
    public static final String CONTACT_MESSAGE_KEY = "contact.message";
    public static final String CONNECTOR_NAME_KEY = "connector.name";
    private final String name;
    private final PublicKey key;
    private final Properties metadata;
    private final String pubKeyFile;
    private final String bundleFile;
    private String infoFile;

    public LicenseProviderData(String vendorName, String connectorName, Key muleDecryptionKey) throws InvalidKeyException {
        if (StringUtils.isBlank((String)vendorName)) {
            throw new IllegalArgumentException("Vendor name cannot be blank");
        }
        this.name = vendorName + "-" + connectorName;
        this.infoFile = this.name.concat(".info");
        this.bundleFile = this.name.concat(".key");
        this.pubKeyFile = this.name.concat(".pub");
        ZippedBundle keyBundle = this.unzipBundle(this.bundleFile);
        this.key = this.loadPublicKey(muleDecryptionKey, keyBundle);
        this.metadata = this.loadMetaData(muleDecryptionKey, keyBundle);
        if (!this.getProperty(CONNECTOR_NAME_KEY).isPresent()) {
            throw new InvalidKeyException("Provided key is incomplete. No connector name information found");
        }
        if (!StringUtils.equals((String)((String)this.getProperty(CONNECTOR_NAME_KEY).get()), (String)connectorName)) {
            throw new InvalidKeyException("Provided key " + this.name + " is not valid for this connector. It was created for " + (String)this.getProperty(CONNECTOR_NAME_KEY).get() + " but expected to be " + connectorName);
        }
    }

    public PublicKey getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return (String)this.getProperty(CONTACT_EMAIL_KEY).get();
    }

    public String getContactMessage() {
        return (String)this.getProperty(CONTACT_MESSAGE_KEY).get();
    }

    public Optional<String> getProperty(String propertyName) {
        return Optional.fromNullable((Object)this.metadata.getProperty(propertyName));
    }

    private Properties loadMetaData(Key muleDecryptionKey, ZippedBundle bundleFiles) throws InvalidKeyException {
        Optional<byte[]> info = bundleFiles.get(this.infoFile);
        if (!info.isPresent()) {
            throw new InvalidKeyException("Failed to decrypt " + this.pubKeyFile + ". No metadata file named  " + this.infoFile + "  found in " + this.bundleFile);
        }
        try {
            Properties metadata = new Properties();
            metadata.load(new ByteArrayInputStream(SecurityUtils.decrypt((byte[])info.get(), muleDecryptionKey)));
            return metadata;
        }
        catch (Exception e) {
            throw new InvalidKeyException("Failed to load " + this.infoFile + " found in " + this.bundleFile, e);
        }
    }

    private PublicKey loadPublicKey(Key muleDecryptionKey, ZippedBundle bundleFiles) throws InvalidKeyException {
        Optional<byte[]> pub = bundleFiles.get(this.pubKeyFile);
        if (!pub.isPresent()) {
            throw new InvalidKeyException("Failed to decrypt " + this.pubKeyFile + ". No public key name " + this.pubKeyFile + " found in bundle " + this.bundleFile);
        }
        try {
            return SecurityUtils.loadPublic(SecurityUtils.decrypt((byte[])pub.get(), muleDecryptionKey));
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new InvalidKeyException("Failed to decrypt " + this.pubKeyFile + " found in " + this.bundleFile, e);
        }
    }

    private ZippedBundle unzipBundle(String file) {
        InputStream bundleIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (bundleIS == null && (bundleIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("license/" + file)) == null) {
            throw new IllegalArgumentException("Vendor key bundle for name [" + file + "] not found as resource");
        }
        return new ZippedBundle(bundleIS);
    }
}
