/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package org.mule.devkit.3.9.0.internal.lic.validator;

import org.apache.log4j.Logger;
import org.mule.devkit.3.9.0.internal.lic.model.Entitlement;
import org.mule.devkit.3.9.0.internal.lic.validator.LicenseValidator;

public class TestValidator
implements LicenseValidator {
    private static final Logger logger = Logger.getLogger(TestValidator.class);

    @Override
    public void checkEnterpriseLicense(boolean allowEvaluation) {
        logger.debug((Object)("Checking EE license under testing.mode. AllowEvaluation=" + allowEvaluation));
    }

    @Override
    public void checkEntitlement(Entitlement entitlement) {
        logger.debug((Object)"Checking Entitlement under testing.mode");
        logger.debug((Object)"Entitlement info: ");
        logger.debug((Object)("  id: " + entitlement.id()));
        logger.debug((Object)("  provider: " + entitlement.provider() + " -> thirdParty: " + entitlement.isThirdParty()));
        logger.debug((Object)("  version: " + entitlement.version()));
        logger.debug((Object)("  description: " + entitlement.description()));
        logger.debug((Object)("  license name: " + entitlement.licenseName()));
    }
}
