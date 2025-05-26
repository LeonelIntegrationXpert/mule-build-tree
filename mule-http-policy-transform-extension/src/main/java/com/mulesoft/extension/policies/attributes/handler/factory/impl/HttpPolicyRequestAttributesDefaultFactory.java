/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyRequestAttributesFactory
 *  org.mule.extension.http.api.policy.HttpPolicyRequestAttributes
 *  org.mule.runtime.api.util.MultiMap
 */
package com.mulesoft.extension.policies.attributes.handler.factory.impl;

import com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyRequestAttributesFactory;
import org.mule.extension.http.api.policy.HttpPolicyRequestAttributes;
import org.mule.runtime.api.util.MultiMap;

public class HttpPolicyRequestAttributesDefaultFactory
implements HttpPolicyRequestAttributesFactory {
    public HttpPolicyRequestAttributes from(HttpPolicyRequestAttributes attributes, MultiMap<String, String> headers) {
        return new HttpPolicyRequestAttributes(headers, attributes.getQueryParams(), attributes.getUriParams(), attributes.getRequestPath());
    }
}
