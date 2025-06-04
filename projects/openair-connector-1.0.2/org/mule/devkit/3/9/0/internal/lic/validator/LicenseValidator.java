/*
 * Decompiled with CFR 0.152.
 */
package org.mule.devkit.3.9.0.internal.lic.validator;

import org.mule.devkit.3.9.0.internal.lic.model.Entitlement;

public interface LicenseValidator {
    public void checkEnterpriseLicense(boolean var1);

    public void checkEntitlement(Entitlement var1);
}
