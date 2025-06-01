/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.mulesoft.extension.policies.attributes.tools;

import com.mulesoft.extension.policies.attributes.tools.FieldInspector;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldCopier {
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldCopier.class);
    private final List<Field> fieldsToCopy = new LinkedList<Field>();

    public FieldCopier(Class clazz, Collection<String> fieldsToIgnore) {
        new FieldInspector().getFields(clazz).stream().filter(field -> !fieldsToIgnore.contains(field.getName())).forEach(field -> {
            field.setAccessible(true);
            this.fieldsToCopy.add((Field)field);
        });
    }

    public void copyFields(Object source, Object target) {
        this.fieldsToCopy.forEach(field -> {
            try {
                field.set(target, field.get(source));
            }
            catch (IllegalAccessException e) {
                LOGGER.debug(String.format("Could not copy field {}.", field.getName()), (Throwable)e);
            }
        });
    }
}
