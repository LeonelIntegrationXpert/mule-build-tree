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
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrossClassFieldCopier {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrossClassFieldCopier.class);
    private final Map<String, Field> sourceFields = new HashMap<String, Field>();
    private final Map<String, Field> targetFields = new HashMap<String, Field>();

    public CrossClassFieldCopier(Class sourceClazz, Class targetClazz, Collection<String> fieldsToIgnore) {
        FieldInspector inspector = new FieldInspector();
        inspector.getFields(sourceClazz).stream().filter(field -> !fieldsToIgnore.contains(field.getName())).forEach(field -> {
            field.setAccessible(true);
            this.sourceFields.put(field.getName(), (Field)field);
        });
        inspector.getFields(targetClazz).stream().filter(field -> !fieldsToIgnore.contains(field.getName())).forEach(field -> {
            if (this.sourceFields.get(field.getName()) != null) {
                field.setAccessible(true);
                this.targetFields.put(field.getName(), (Field)field);
            } else {
                LOGGER.warn("Class {} doesn't have a field matching the {} field declared in {}.", new Object[]{sourceClazz.getName(), field.getName(), targetClazz.getName()});
            }
        });
        if (!this.sourceFields.keySet().containsAll(this.targetFields.keySet()) || !this.targetFields.keySet().containsAll(this.sourceFields.keySet())) {
            LOGGER.warn("Class {} and {} have different declared fields. Coping values between them will proceed as best effort.", (Object)sourceClazz.getName(), (Object)targetClazz.getName());
        }
    }

    public void copyFields(Object source, Object target) {
        this.targetFields.forEach((key, value) -> {
            try {
                value.set(target, this.sourceFields.get(key).get(source));
            }
            catch (IllegalAccessException e) {
                LOGGER.debug(String.format("Could not copy field %s.", key), (Throwable)e);
            }
        });
    }
}
