/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.HttpPolicyTransformErrorTypes
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesFactory
 *  org.mule.extension.http.api.policy.HttpPolicyResponseAttributes
 *  org.mule.runtime.api.util.MultiMap
 *  org.mule.runtime.extension.api.error.ErrorTypeDefinition
 *  org.mule.runtime.extension.api.exception.ModuleException
 */
package com.mulesoft.extension.policies.attributes.handler;

import com.mulesoft.extension.policies.attributes.HttpPolicyTransformErrorTypes;
import com.mulesoft.extension.policies.attributes.handler.HttpAttributesHandler;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesFactory;
import java.util.List;
import org.mule.extension.http.api.policy.HttpPolicyResponseAttributes;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.exception.ModuleException;

public class HttpPolicyResponseAttributesHandler
extends HttpAttributesHandler<HttpPolicyResponseAttributes> {
    HttpPolicyResponseAttributesFactory factory;

    public HttpPolicyResponseAttributesHandler(HttpPolicyResponseAttributesFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object addResponseHeaders(HttpPolicyResponseAttributes attributes, MultiMap<String, String> headers) {
        return this.factory.from(attributes, this.mergeHeaders((MultiMap<String, String>)attributes.getHeaders(), headers));
    }

    @Override
    public Object addRequestHeaders(HttpPolicyResponseAttributes attributes, MultiMap<String, String> headers) {
        throw new ModuleException("Request headers can not be added when attributes are of type " + HttpPolicyResponseAttributes.class.getSimpleName(), (ErrorTypeDefinition)HttpPolicyTransformErrorTypes.INVALID_INPUT);
    }

    @Override
    public Object removeHeaders(HttpPolicyResponseAttributes attributes, List<String> headerNames) {
        return this.factory.from(attributes, this.removeHeaders((MultiMap<String, String>)attributes.getHeaders(), headerNames));
    }
}
