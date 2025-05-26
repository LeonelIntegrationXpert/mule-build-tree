/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mulesoft.extension.policies.attributes.handler.HttpAttributesHandler
 *  com.mulesoft.extension.policies.attributes.tools.FieldInspector
 */
package com.mulesoft.extension.policies.attributes.handler.factory;

import com.mulesoft.extension.policies.attributes.handler.HttpAttributesHandler;
import com.mulesoft.extension.policies.attributes.tools.FieldInspector;
import java.util.Collection;

public abstract class AttributesHandlerFactory {
    private final Class clazz;
    private final Collection<String> knownFields;

    public AttributesHandlerFactory(Class clazz, Collection<String> knownFields) {
        this.clazz = clazz;
        this.knownFields = knownFields;
    }

    public HttpAttributesHandler create() {
        return this.useDefaultImplementation() ? this.defaultImplementation() : this.reflectiveImplementation();
    }

    private boolean useDefaultImplementation() {
        return new FieldInspector().getFields(this.clazz).stream().allMatch(field -> this.knownFields.contains(field.getName()));
    }

    protected abstract HttpAttributesHandler defaultImplementation();

    protected abstract HttpAttributesHandler reflectiveImplementation();
}
