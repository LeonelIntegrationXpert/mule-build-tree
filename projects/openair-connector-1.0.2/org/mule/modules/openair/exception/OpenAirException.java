/*
 * Decompiled with CFR 0.152.
 */
package org.mule.modules.openair.exception;

public class OpenAirException
extends RuntimeException {
    public OpenAirException(String message) {
        super(message);
    }

    public OpenAirException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenAirException(Throwable cause) {
        super(cause);
    }
}
