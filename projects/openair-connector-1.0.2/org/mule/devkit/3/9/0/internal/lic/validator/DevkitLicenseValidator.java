/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package org.mule.devkit.3.9.0.internal.lic.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;
import org.mule.devkit.3.9.0.internal.lic.SecurityUtils;
import org.mule.devkit.3.9.0.internal.lic.exception.InvalidKeyException;
import org.mule.devkit.3.9.0.internal.lic.exception.InvalidLicenseException;
import org.mule.devkit.3.9.0.internal.lic.model.CustomLicense;
import org.mule.devkit.3.9.0.internal.lic.model.Entitlement;
import org.mule.devkit.3.9.0.internal.lic.model.LicenseProviderData;
import org.mule.devkit.3.9.0.internal.lic.validator.LicenseValidator;

public class DevkitLicenseValidator
implements LicenseValidator {
    private static final Logger logger = Logger.getLogger(DevkitLicenseValidator.class);
    private static final String LICENSE_MANAGEMENT_FACTORY = "com.mulesource.licm.LicenseManagementFactory";
    private static final String ENTERPRISE_LICENSE_KEY = "com.mulesource.licm.EnterpriseLicenseKey";
    private static final String EXPIRED_LICENSE_MSG = "Your license has expired";
    private static final String FEATURE_VALIDATOR = "com.mulesource.licm.feature.FeatureValidator";
    private static final String LICENSE_MANAGER = "com.mulesource.licm.LicenseManager";
    private static final String FEATURE = "com.mulesource.licm.feature.Feature";
    private static final String MULE_EE = "mule-ee";
    private static final String MULE_PUB_KEY = "mule.pub";
    private PublicKey mulePublicKey;
    private String DEFAULT_EXCEPTION_MSG;
    private String EVALUATION_LICENSE_MSG;
    private String MISSING_ENTITLEMENT_MSG;
    private String moduleName;
    private Object license;
    private Constructor<?> featureConstructor;
    private Constructor<?> featureValidatorConstructor;
    private Method hasFeature;
    private Method getFeatures;
    private Method licenseIsEvaluation;
    private Method licenseGetExpirationDate;
    private Method validateFeature;
    private Method setFeature;

    public DevkitLicenseValidator(String moduleName) {
        this.DEFAULT_EXCEPTION_MSG = String.format("The Module %s requires an Enterprise License. Switch to a Mule-EE runtime to enable it. ", moduleName);
        this.EVALUATION_LICENSE_MSG = String.format("The Module %s does not allow Evaluation Licenses", moduleName);
        this.MISSING_ENTITLEMENT_MSG = String.format("The Module %s requires a license with entitlement for ", moduleName);
        this.moduleName = moduleName;
        try {
            Class<?> licManagerFactory = Class.forName(LICENSE_MANAGEMENT_FACTORY);
            Object licFactory = this.invoke(licManagerFactory.getMethod("getInstance", new Class[0]), null, new Object[0]);
            Object licenseManager = this.invoke(licManagerFactory.getMethod("createLicenseManager", String.class), licFactory, MULE_EE);
            this.license = this.invoke(Class.forName(LICENSE_MANAGER).getMethod("validate", String.class), licenseManager, MULE_EE);
            this.initializeReflectiveMethods();
            this.mulePublicKey = SecurityUtils.loadPublic(MULE_PUB_KEY);
        }
        catch (Exception e) {
            throw new InvalidLicenseException(this.DEFAULT_EXCEPTION_MSG.concat(e.getMessage()));
        }
    }

    private void initializeReflectiveMethods() throws NoSuchMethodException, ClassNotFoundException {
        Class<?> enterpriseLicenseKeyClass = Class.forName(ENTERPRISE_LICENSE_KEY);
        this.getFeatures = enterpriseLicenseKeyClass.getMethod("getFeatures", new Class[0]);
        Class<?> featureClass = Class.forName(FEATURE);
        this.hasFeature = Class.forName("com.mulesource.licm.feature.FeatureSet").getMethod("hasFeature", featureClass);
        this.featureConstructor = featureClass.getConstructor(String.class, String.class);
        this.featureValidatorConstructor = Class.forName(FEATURE_VALIDATOR).getConstructor(featureClass);
        this.licenseGetExpirationDate = enterpriseLicenseKeyClass.getMethod("getExpirationDate", new Class[0]);
        this.licenseIsEvaluation = enterpriseLicenseKeyClass.getMethod("isEvaluation", new Class[0]);
        this.validateFeature = Class.forName(FEATURE_VALIDATOR).getMethod("validate", enterpriseLicenseKeyClass);
        this.setFeature = enterpriseLicenseKeyClass.getMethod("setFeature", featureClass);
    }

    @Override
    public void checkEnterpriseLicense(boolean allowEvaluation) {
        logger.debug((Object)("Checking EE license. Allows Evaluation [" + allowEvaluation + "]"));
        Calendar expirationDate = Calendar.getInstance();
        Object expirationTime = this.invoke(this.licenseGetExpirationDate, this.license, new Object[0]);
        if (expirationTime != null) {
            expirationDate.setTime((Date)expirationTime);
            if (expirationDate.after(new Date())) {
                throw new InvalidLicenseException(EXPIRED_LICENSE_MSG);
            }
        }
        Boolean isEvaluation = (Boolean)this.invoke(this.licenseIsEvaluation, this.license, new Object[0]);
        if (!allowEvaluation && isEvaluation.booleanValue()) {
            throw new InvalidLicenseException(this.EVALUATION_LICENSE_MSG);
        }
    }

    @Override
    public void checkEntitlement(Entitlement requiredEntitlement) {
        logger.debug((Object)("Entitlement is third party " + requiredEntitlement.isThirdParty()));
        if (requiredEntitlement.isThirdParty()) {
            this.addCustomEntitlement(requiredEntitlement);
        }
        logger.debug((Object)("Verify License for entitlement " + requiredEntitlement.id()));
        this.verifyLicenseEntitlements(requiredEntitlement);
    }

    private void verifyLicenseEntitlements(Entitlement requiredEntitlement) {
        Object feature = this.getInstance(this.featureConstructor, requiredEntitlement.id(), requiredEntitlement.description());
        logger.debug((Object)("FEATURE VALIDATOR EXECUTE" + requiredEntitlement.id()));
        this.invoke(this.MISSING_ENTITLEMENT_MSG + requiredEntitlement.id(), this.validateFeature, this.getInstance(this.featureValidatorConstructor, feature), this.license);
    }

    private void addCustomEntitlement(Entitlement entitlement) {
        logger.debug((Object)("Entitlement is present in License " + this.isPresentInLicense(entitlement)));
        if (!this.isPresentInLicense(entitlement).booleanValue()) {
            CustomLicense customLicense;
            LicenseProviderData licenseProviderData = null;
            try {
                logger.debug((Object)("Loading provider data for entitlement " + entitlement.id()));
                licenseProviderData = new LicenseProviderData(entitlement.provider(), entitlement.licenseName(), this.mulePublicKey);
                logger.debug((Object)("Loading custom license information from license file " + entitlement.licenseName()));
                customLicense = new CustomLicense(entitlement.id(), entitlement.licenseName(), licenseProviderData);
            }
            catch (InvalidKeyException e) {
                String email = licenseProviderData != null ? licenseProviderData.getEmail() : "";
                String msg = licenseProviderData != null ? licenseProviderData.getContactMessage() : "";
                throw new InvalidLicenseException(this.MISSING_ENTITLEMENT_MSG + entitlement.id() + ". Contact email: " + email + ". " + msg);
            }
            logger.debug((Object)("Validating custom Entitlement " + entitlement.id()));
            if (!customLicense.isValid(entitlement)) {
                this.logInvalidLicenseError(entitlement, licenseProviderData, customLicense);
                throw new InvalidLicenseException(this.MISSING_ENTITLEMENT_MSG + entitlement.id() + ". " + licenseProviderData.getContactMessage() + " Please Contact: " + licenseProviderData.getEmail());
            }
            logger.debug((Object)("Adding custom entitlement " + entitlement.id()));
            this.addFeature(entitlement.id(), entitlement.description());
        }
    }

    private Boolean isPresentInLicense(Entitlement entitlement) {
        boolean isCloudHub = true;
        try {
            Class.forName("com.cloudhub.extensions.tracking.NotificationHandler");
        }
        catch (Exception e) {
            isCloudHub = false;
        }
        logger.debug((Object)("Environment is CloudHub " + isCloudHub));
        return !isCloudHub && (Boolean)this.invoke(this.hasFeature, this.invoke(this.getFeatures, this.license, new Object[0]), this.getInstance(this.featureConstructor, entitlement.id(), entitlement.description())) != false;
    }

    private void addFeature(String id, String description) {
        this.invoke(this.setFeature, this.license, this.getInstance(this.featureConstructor, id, description));
    }

    private Object invoke(Method method, Object instance, Object ... args) {
        return this.invoke(this.DEFAULT_EXCEPTION_MSG, method, instance, args);
    }

    private Object invoke(String msgOnException, Method method, Object instance, Object ... args) {
        try {
            return method.invoke(instance, args);
        }
        catch (IllegalAccessException e) {
            return new InvalidLicenseException(msgOnException, e);
        }
        catch (InvocationTargetException e) {
            throw this.loggedException(e.getTargetException().getMessage(), new InvalidLicenseException(msgOnException));
        }
    }

    private void logInvalidLicenseError(Entitlement entitlement, LicenseProviderData licenseProviderData, CustomLicense customLicense) {
        String licName = entitlement.licenseName().concat(".lic");
        if (!customLicense.hasValidVersion(entitlement.version())) {
            logger.error((Object)("Your license " + licName + " is not valid for this connector version: " + entitlement.version()));
        } else if (!customLicense.hasValidFeature()) {
            logger.error((Object)("Your license " + licName + "does not enable the feature [" + entitlement.id() + "] required by the module " + this.moduleName));
        } else {
            logger.error((Object)("Your license " + licName + " has expired on the " + (String)customLicense.getProperty("expiration.date").get()));
        }
        logger.error((Object)("Please get in contact with your Vendor " + licenseProviderData.getName() + " using the following address: " + (String)licenseProviderData.getProperty("contact.email").get()));
        if (licenseProviderData.getProperty("contact.message").isPresent()) {
            logger.error(licenseProviderData.getProperty("contact.message").get());
        }
    }

    public Object getInstance(Constructor<?> constructor, Object ... args) {
        try {
            return constructor.newInstance(args);
        }
        catch (InstantiationException e) {
            throw new InvalidLicenseException(this.DEFAULT_EXCEPTION_MSG, e);
        }
        catch (IllegalAccessException e) {
            throw new InvalidLicenseException(this.DEFAULT_EXCEPTION_MSG, e);
        }
        catch (InvocationTargetException e) {
            throw this.loggedException(e.getTargetException().getMessage(), new InvalidLicenseException(this.DEFAULT_EXCEPTION_MSG.concat(e.getMessage())));
        }
    }

    private RuntimeException loggedException(String loggedMessage, RuntimeException e) {
        logger.debug((Object)loggedMessage);
        return e;
    }
}
