/*
 * Decompiled with CFR 0.152.
 */
package org.mule.modules.openair.utils;

public class MethodAccessibilityException
extends RuntimeException {
    private final String accessedMethod;

    public MethodAccessibilityException(String accessedMethod, Throwable cause) {
        super(String.format("An error occurred while accessing the method '%s'. Please check if this method exists and has 0 arguments.", accessedMethod), cause);
        this.accessedMethod = accessedMethod;
    }

    public String getAccessedMethod() {
        return this.accessedMethod;
    }
}
