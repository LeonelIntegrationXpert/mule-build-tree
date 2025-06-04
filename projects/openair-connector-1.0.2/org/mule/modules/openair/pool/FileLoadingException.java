/*
 * Decompiled with CFR 0.152.
 */
package org.mule.modules.openair.pool;

import org.mule.modules.openair.exception.OpenAirException;

public class FileLoadingException
extends OpenAirException {
    private final String operationName;

    public FileLoadingException(String operationName, Throwable cause) {
        super(String.format("File for operation '%s' could not be accessed. Please check if it's available.", operationName), cause);
        this.operationName = operationName;
    }

    public String getOperationName() {
        return this.operationName;
    }
}
