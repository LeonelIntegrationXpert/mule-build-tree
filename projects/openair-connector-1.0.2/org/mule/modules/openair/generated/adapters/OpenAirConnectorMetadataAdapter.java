/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MetadataAware
 */
package org.mule.modules.openair.generated.adapters;

import org.mule.api.MetadataAware;
import org.mule.modules.openair.generated.adapters.OpenAirConnectorCapabilitiesAdapter;

public class OpenAirConnectorMetadataAdapter
extends OpenAirConnectorCapabilitiesAdapter
implements MetadataAware {
    private static final String MODULE_NAME = "OpenAir";
    private static final String MODULE_VERSION = "1.0.2";
    private static final String DEVKIT_VERSION = "3.9.0";
    private static final String DEVKIT_BUILD = "UNNAMED.2793.f49b6c7";
    private static final String MIN_MULE_VERSION = "3.7";

    public String getModuleName() {
        return MODULE_NAME;
    }

    public String getModuleVersion() {
        return MODULE_VERSION;
    }

    public String getDevkitVersion() {
        return DEVKIT_VERSION;
    }

    public String getDevkitBuild() {
        return DEVKIT_BUILD;
    }

    public String getMinMuleVersion() {
        return MIN_MULE_VERSION;
    }
}
