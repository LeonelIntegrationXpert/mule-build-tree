/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyRequestAttributesFactory
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpRequesterRequestAttributesHandlerFactory
 *  com.mulesoft.extension.policies.attributes.tools.FieldCopier
 *  org.mule.extension.http.api.policy.HttpPolicyRequestAttributes
 *  org.mule.runtime.api.util.MultiMap
 */
package com.mulesoft.extension.policies.attributes.handler.factory.impl;

import com.mulesoft.extension.policies.attributes.handler.factory.HttpPolicyRequestAttributesFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpRequesterRequestAttributesHandlerFactory;
import com.mulesoft.extension.policies.attributes.tools.FieldCopier;
import java.util.Collection;
import org.mule.extension.http.api.policy.HttpPolicyRequestAttributes;
import org.mule.runtime.api.util.MultiMap;

public class HttpPolicyRequestAttributesReflectiveFactory
implements HttpPolicyRequestAttributesFactory {
    private final FieldCopier fieldCopier = new FieldCopier(HttpPolicyRequestAttributes.class, (Collection)HttpRequesterRequestAttributesHandlerFactory.HTTP_POLICY_REQUEST_ATTRIBUTES_KNOWN_FIELDS);

    public HttpPolicyRequestAttributes from(HttpPolicyRequestAttributes attributes, MultiMap<String, String> headers) {
        HttpPolicyRequestAttributes copy = new HttpPolicyRequestAttributes(headers, attributes.getQueryParams(), attributes.getUriParams(), attributes.getRequestPath());
        this.fieldCopier.copyFields((Object)attributes, (Object)copy);
        return copy;
    }
}
