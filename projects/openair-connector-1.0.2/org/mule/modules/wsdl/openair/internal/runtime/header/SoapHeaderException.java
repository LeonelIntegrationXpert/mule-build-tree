/*
 * Decompiled with CFR 0.152.
 */
package org.mule.modules.wsdl.openair.internal.runtime.header;

public class SoapHeaderException
extends Exception {
    private static final long serialVersionUID = 7322894771315550250L;

    public SoapHeaderException() {
    }

    public SoapHeaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoapHeaderException(String message) {
        super(message);
    }

    public SoapHeaderException(Throwable cause) {
        super(cause);
    }
}
