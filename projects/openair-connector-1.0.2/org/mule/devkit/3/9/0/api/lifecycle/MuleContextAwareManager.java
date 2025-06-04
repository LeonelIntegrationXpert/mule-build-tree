/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleContext
 *  org.mule.api.context.MuleContextAware
 */
package org.mule.devkit.3.9.0.api.lifecycle;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;

public class MuleContextAwareManager {
    public static void setMuleContext(Object o, MuleContext muleContext) {
        if (o instanceof MuleContextAware) {
            ((MuleContextAware)o).setMuleContext(muleContext);
        }
    }
}
