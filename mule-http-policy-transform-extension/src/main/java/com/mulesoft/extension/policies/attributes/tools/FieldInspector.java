/*
 * Decompiled with CFR 0.152.
 */
package com.mulesoft.extension.policies.attributes.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FieldInspector {
    public List<Field> getFields(Class clazz) {
        if (clazz.equals(Object.class)) {
            return Collections.emptyList();
        }
        LinkedList<Field> fields = new LinkedList<Field>(this.getFields(clazz.getSuperclass()));
        Arrays.stream(clazz.getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(fields::add);
        return fields;
    }
}
