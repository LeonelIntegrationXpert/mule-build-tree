/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.extension.http.api.HttpResponseAttributes
 *  org.mule.runtime.api.util.MultiMap
 */
package com.mulesoft.extension.policies.attributes.handler.factory;

import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.runtime.api.util.MultiMap;

public interface HttpResponseAttributesFactory {
    public HttpResponseAttributes from(HttpResponseAttributes var1, MultiMap<String, String> var2);
}
