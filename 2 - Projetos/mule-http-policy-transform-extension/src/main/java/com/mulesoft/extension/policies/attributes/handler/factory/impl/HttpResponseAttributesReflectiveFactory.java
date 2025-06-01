/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpResponseAttributesFactory
 *  com.mulesoft.extension.policies.attributes.handler.factory.HttpResponseAttributesHandlerFactory
 *  com.mulesoft.extension.policies.attributes.tools.FieldCopier
 *  org.mule.extension.http.api.HttpResponseAttributes
 *  org.mule.runtime.api.util.MultiMap
 */
package com.mulesoft.extension.policies.attributes.handler.factory.impl;

import com.mulesoft.extension.policies.attributes.handler.factory.HttpResponseAttributesFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpResponseAttributesHandlerFactory;
import com.mulesoft.extension.policies.attributes.tools.FieldCopier;
import java.util.Collection;
import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.runtime.api.util.MultiMap;

public class HttpResponseAttributesReflectiveFactory
implements HttpResponseAttributesFactory {
    private final FieldCopier fieldCopier = new FieldCopier(HttpResponseAttributes.class, (Collection)HttpResponseAttributesHandlerFactory.HTTP_RESPONSE_ATTRIBUTES_KNOWN_FIELDS);

    public HttpResponseAttributes from(HttpResponseAttributes attributes, MultiMap<String, String> headers) {
        HttpResponseAttributes copy = new HttpResponseAttributes(attributes.getStatusCode(), attributes.getReasonPhrase(), headers);
        this.fieldCopier.copyFields((Object)attributes, (Object)copy);
        return copy;
    }
}
