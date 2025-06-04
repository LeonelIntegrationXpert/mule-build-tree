/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.ui.plugin.AbstractUIPlugin
 *  org.osgi.framework.BundleContext
 */
package org.mule.tooling.ui.contribution;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class OpenairActivator
extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "org.mule.tooling.ui.contribution.openair";
    private static OpenairActivator plugin;

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static OpenairActivator getDefault() {
        return plugin;
    }
}
