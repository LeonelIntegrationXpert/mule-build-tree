/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package org.mule.devkit.3.9.0.internal.lic;

import org.apache.log4j.Logger;
import org.mule.devkit.3.9.0.internal.lic.validator.DevkitLicenseValidator;
import org.mule.devkit.3.9.0.internal.lic.validator.LicenseValidator;
import org.mule.devkit.3.9.0.internal.lic.validator.TestValidator;

public abstract class LicenseValidatorFactory {
    private static final String MULE_TESTING_MODE = "mule.testingMode";
    private static Logger log = Logger.getLogger(LicenseValidatorFactory.class);

    public static LicenseValidator getValidator(String moduleName) {
        log.debug((Object)("USING TESTING VALIDATOR : " + LicenseValidatorFactory.isTestMode()));
        return LicenseValidatorFactory.isTestMode() ? new TestValidator() : new DevkitLicenseValidator(moduleName);
    }

    private static boolean isTestMode() {
        return System.getProperty(MULE_TESTING_MODE) != null;
    }
}
