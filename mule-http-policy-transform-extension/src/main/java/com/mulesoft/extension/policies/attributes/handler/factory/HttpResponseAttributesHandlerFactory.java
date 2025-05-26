/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.HttpResponseAttributesHandler
 *  com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpResponseAttributesDefaultFactory
 *  com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpResponseAttributesReflectiveFactory
 *  org.mule.extension.http.api.HttpResponseAttributes
 */
package com.mulesoft.extension.policies.attributes.handler.factory;

import com.mulesoft.extension.policies.attributes.handler.HttpResponseAttributesHandler;
import com.mulesoft.extension.policies.attributes.handler.factory.AttributesHandlerFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.HttpResponseAttributesFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpResponseAttributesDefaultFactory;
import com.mulesoft.extension.policies.attributes.handler.factory.impl.HttpResponseAttributesReflectiveFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.mule.extension.http.api.HttpResponseAttributes;

public class HttpResponseAttributesHandlerFactory
extends AttributesHandlerFactory {
    public static final Set<String> HTTP_RESPONSE_ATTRIBUTES_KNOWN_FIELDS = new HashSet<String>(Arrays.asList("statusCode", "reasonPhrase", "headers"));

    public HttpResponseAttributesHandlerFactory() {
        super(HttpResponseAttributes.class, HTTP_RESPONSE_ATTRIBUTES_KNOWN_FIELDS);
    }

    protected HttpResponseAttributesHandler defaultImplementation() {
        return new HttpResponseAttributesHandler((HttpResponseAttributesFactory)new HttpResponseAttributesDefaultFactory());
    }

    protected HttpResponseAttributesHandler reflectiveImplementation() {
        return new HttpResponseAttributesHandler((HttpResponseAttributesFactory)new HttpResponseAttributesReflectiveFactory());
    }
}
