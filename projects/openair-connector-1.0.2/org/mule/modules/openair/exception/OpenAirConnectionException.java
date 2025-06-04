/*
 * Decompiled with CFR 0.152.
 */
package org.mule.modules.openair.exception;

import org.mule.modules.openair.exception.OpenAirException;

public class OpenAirConnectionException
extends OpenAirException {
    public OpenAirConnectionException(String message) {
        super(message);
    }

    public OpenAirConnectionException(String operationName, Throwable cause) {
        super(String.format("A connection error occurred while executing the operation '%s'.", operationName), cause);
    }
}
