/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.ConnectionException
 *  org.mule.api.ConnectionExceptionCode
 */
package org.mule.devkit.3.9.0.api.exception;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;

public class ConfigurationWarning
extends ConnectionException {
    public ConfigurationWarning(String message) {
        super(ConnectionExceptionCode.UNKNOWN, "", message);
    }
}
