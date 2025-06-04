/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.devkit.ProcessAdapter
 *  org.mule.api.devkit.ProcessTemplate
 *  org.mule.api.lifecycle.Initialisable
 *  org.mule.api.lifecycle.InitialisationException
 */
package org.mule.modules.openair.generated.adapters;

import org.mule.api.devkit.ProcessAdapter;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.devkit.3.9.0.internal.lic.LicenseValidatorFactory;
import org.mule.devkit.3.9.0.internal.lic.validator.LicenseValidator;
import org.mule.modules.openair.generated.adapters.OpenAirConnectorCapabilitiesAdapter;
import org.mule.modules.openair.generated.adapters.OpenAirConnectorLifecycleInjectionAdapter;

public class OpenAirConnectorProcessAdapter
extends OpenAirConnectorLifecycleInjectionAdapter
implements ProcessAdapter<OpenAirConnectorCapabilitiesAdapter>,
Initialisable {
    public <P> ProcessTemplate<P, OpenAirConnectorCapabilitiesAdapter> getProcessTemplate() {
        OpenAirConnectorProcessAdapter object = this;
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    @Override
    public void initialise() throws InitialisationException {
        super.initialise();
        this.checkMuleLicense();
    }

    private void checkMuleLicense() {
        LicenseValidator licenseValidator = LicenseValidatorFactory.getValidator("OpenAir");
        licenseValidator.checkEnterpriseLicense(true);
    }
}
