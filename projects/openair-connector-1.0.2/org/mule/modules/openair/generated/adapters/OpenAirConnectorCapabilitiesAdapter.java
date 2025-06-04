/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.devkit.capability.Capabilities
 *  org.mule.api.devkit.capability.ModuleCapability
 */
package org.mule.modules.openair.generated.adapters;

import org.mule.api.devkit.capability.Capabilities;
import org.mule.api.devkit.capability.ModuleCapability;
import org.mule.modules.openair.OpenAirConnector;

public class OpenAirConnectorCapabilitiesAdapter
extends OpenAirConnector
implements Capabilities {
    public boolean isCapableOf(ModuleCapability capability) {
        if (capability == ModuleCapability.LIFECYCLE_CAPABLE) {
            return true;
        }
        return capability == ModuleCapability.CONNECTION_MANAGEMENT_CAPABLE;
    }
}
