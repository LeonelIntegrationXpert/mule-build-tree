/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.HttpPolicyTransformErrorTypes
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesHandlerFactory
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpRequesterRequestAttributesHandlerFactory
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpResponseAttributesHandlerFactory
 *  org.mule.extension.http.api.HttpRequestAttributes
 *  org.mule.extension.http.api.HttpResponseAttributes
 *  org.mule.extension.http.api.policy.HttpPolicyRequestAttributes
 *  org.mule.extension.http.api.policy.HttpPolicyResponseAttributes
 *  org.mule.runtime.extension.api.error.ErrorTypeDefinition
 *  org.mule.runtime.extension.api.exception.ModuleException
 */
package com.mulesoft.extension.policies.attributes.handler;

import com.mulesoft.extension.policies.attributes.HttpPolicyTransformErrorTypes;
import com.mulesoft.extension.policies.attributes.handler.HttpAttributesHandler;
import com.mulesoft.extension.policies.attributes.handler.HttpRequestAttributesHandler;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyResponseAttributesHandlerFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpRequesterRequestAttributesHandlerFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpResponseAttributesHandlerFactory;
import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.extension.http.api.policy.HttpPolicyRequestAttributes;
import org.mule.extension.http.api.policy.HttpPolicyResponseAttributes;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.exception.ModuleException;

public class HttpAttributesHandlerSelector {
    private final HttpRequestAttributesHandler requestAttributesHandler = new HttpRequestAttributesHandler();
    private final HttpAttributesHandler responseAttributesHandler = new HttpResponseAttributesHandlerFactory().create();
    private final HttpAttributesHandler policyResponseAttributesHandler = new HttpPolicyResponseAttributesHandlerFactory().create();
    private final HttpAttributesHandler requesterRequestAttributesHandler = new HttpRequesterRequestAttributesHandlerFactory().create();

    public HttpAttributesHandler get(Object attributes) {
        if (attributes instanceof HttpRequestAttributes) {
            return this.requestAttributesHandler;
        }
        if (attributes instanceof HttpPolicyRequestAttributes) {
            return this.requesterRequestAttributesHandler;
        }
        if (attributes instanceof HttpPolicyResponseAttributes) {
            return this.policyResponseAttributesHandler;
        }
        if (attributes instanceof HttpResponseAttributes) {
            return this.responseAttributesHandler;
        }
        Class<?> clazz = attributes != null ? attributes.getClass() : null;
        throw new ModuleException("Can not add headers to attributes of type " + clazz, (ErrorTypeDefinition)HttpPolicyTransformErrorTypes.INVALID_INPUT);
    }
}
