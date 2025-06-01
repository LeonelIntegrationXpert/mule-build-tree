/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpResponseAttributesFactory
 *  org.mule.extension.http.api.HttpResponseAttributes
 *  org.mule.runtime.api.util.MultiMap
 */
package com.mulesoft.extension.policies.attributes.handler.factory.impl;

import com.mulesoft.extension.policies.attributes.handler.factory.HttpResponseAttributesFactory;
import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.runtime.api.util.MultiMap;

public class HttpResponseAttributesDefaultFactory
implements HttpResponseAttributesFactory {
    public HttpResponseAttributes from(HttpResponseAttributes attributes, MultiMap<String, String> headers) {
        return new HttpResponseAttributes(attributes.getStatusCode(), attributes.getReasonPhrase(), headers);
    }
}
