/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.mule.modules.wsdl.openair.internal.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoapCallException
extends Exception {
    private static final long serialVersionUID = 1965767113473619186L;

    private SoapCallException(@NotNull String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    @NotNull
    static SoapCallException createCallException(@NotNull Throwable e) {
        return new SoapCallException("Invocation could not be completed successfully." + e.getLocalizedMessage(), e);
    }

    @NotNull
    static SoapCallException createMetadataInvocationException(@Nullable String type) {
        return new SoapCallException("SOAP invocation can not be performed. Operation metadata is invalid '" + type + "'. Review the invocation parameters.", null);
    }
}
