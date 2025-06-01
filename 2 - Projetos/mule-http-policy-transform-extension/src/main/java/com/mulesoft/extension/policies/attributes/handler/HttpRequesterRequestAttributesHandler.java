/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyRequestAttributesFactory
 *  org.mule.extension.http.api.policy.HttpPolicyRequestAttributes
 *  org.mule.extension.http.api.policy.HttpPolicyResponseAttributes
 *  org.mule.runtime.api.util.MultiMap
 */
package com.mulesoft.extension.policies.attributes.handler;

import com.mulesoft.extension.policies.attributes.handler.HttpAttributesHandler;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyRequestAttributesFactory;
import java.util.List;
import org.mule.extension.http.api.policy.HttpPolicyRequestAttributes;
import org.mule.extension.http.api.policy.HttpPolicyResponseAttributes;
import org.mule.runtime.api.util.MultiMap;

public class HttpRequesterRequestAttributesHandler
extends HttpAttributesHandler<HttpPolicyRequestAttributes> {
    private HttpPolicyRequestAttributesFactory factory;

    public HttpRequesterRequestAttributesHandler(HttpPolicyRequestAttributesFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object addResponseHeaders(HttpPolicyRequestAttributes attributes, MultiMap<String, String> headers) {
        HttpPolicyResponseAttributes updatedAttributes = new HttpPolicyResponseAttributes();
        updatedAttributes.setHeaders(this.enforceHeaders(headers));
        return updatedAttributes;
    }

    @Override
    public Object addRequestHeaders(HttpPolicyRequestAttributes attributes, MultiMap<String, String> headers) {
        return this.factory.from(attributes, this.mergeHeaders((MultiMap<String, String>)attributes.getHeaders(), headers));
    }

    @Override
    public Object removeHeaders(HttpPolicyRequestAttributes attributes, List<String> headerNames) {
        return this.factory.from(attributes, this.removeHeaders((MultiMap<String, String>)attributes.getHeaders(), headerNames));
    }
}
