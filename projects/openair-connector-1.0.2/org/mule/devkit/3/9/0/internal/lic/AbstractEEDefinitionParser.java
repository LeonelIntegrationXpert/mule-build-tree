/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  org.mule.security.oauth.config.AbstractDevkitBasedDefinitionParser
 */
package org.mule.devkit.3.9.0.internal.lic;

import com.google.common.base.Optional;
import org.mule.devkit.3.9.0.internal.lic.LicenseValidatorFactory;
import org.mule.devkit.3.9.0.internal.lic.model.Entitlement;
import org.mule.devkit.3.9.0.internal.lic.validator.LicenseValidator;
import org.mule.security.oauth.config.AbstractDevkitBasedDefinitionParser;

public abstract class AbstractEEDefinitionParser
extends AbstractDevkitBasedDefinitionParser {
    public AbstractEEDefinitionParser() {
        LicenseValidator licenseValidator = LicenseValidatorFactory.getValidator(this.moduleName());
        licenseValidator.checkEnterpriseLicense(true);
        if (this.entitlement().isPresent()) {
            licenseValidator.checkEntitlement((Entitlement)this.entitlement().get());
        }
    }

    protected abstract String moduleName();

    protected Optional<Entitlement> entitlement() {
        return Optional.absent();
    }
}
