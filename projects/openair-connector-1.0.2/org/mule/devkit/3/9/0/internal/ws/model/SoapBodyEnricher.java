/*
 * Decompiled with CFR 0.152.
 */
package org.mule.devkit.3.9.0.internal.ws.model;

import org.mule.devkit.3.9.0.api.ws.definition.ServiceDefinition;
import org.w3c.dom.Document;

public interface SoapBodyEnricher {
    public Document process(ServiceDefinition var1, String var2, Document var3);
}
