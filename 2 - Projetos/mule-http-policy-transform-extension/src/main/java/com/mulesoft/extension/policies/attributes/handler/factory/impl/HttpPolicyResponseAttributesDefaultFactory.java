/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesFactory
 *  org.mule.extension.http.api.HttpResponseAttributes
 *  org.mule.extension.http.api.policy.HttpPolicyResponseAttributes
 *  org.mule.runtime.api.util.MultiMap
 */
package com.mulesoft.extension.policies.attributes.handler.factory.impl;

import com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesFactory;
import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.extension.http.api.policy.HttpPolicyResponseAttributes;
import org.mule.runtime.api.util.MultiMap;

public class HttpPolicyResponseAttributesDefaultFactory
implements HttpPolicyResponseAttributesFactory {
    public HttpResponseAttributes from(HttpPolicyResponseAttributes attributes, MultiMap<String, String> headers) {
        return new HttpResponseAttributes(attributes.getStatusCode().intValue(), attributes.getReasonPhrase(), headers);
    }
}
