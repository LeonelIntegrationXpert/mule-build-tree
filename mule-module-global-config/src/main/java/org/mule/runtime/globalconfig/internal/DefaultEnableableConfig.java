/*
 * Decompiled with CFR 0.152.
 */
package org.mule.runtime.globalconfig.internal;

import org.mule.runtime.globalconfig.api.EnableableConfig;

class DefaultEnableableConfig
implements EnableableConfig {
    public static final String ENABLED_PROPERTY = "enabled";
    private final boolean enabled;

    public DefaultEnableableConfig(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
