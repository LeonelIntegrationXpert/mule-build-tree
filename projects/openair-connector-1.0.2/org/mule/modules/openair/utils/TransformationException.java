/*
 * Decompiled with CFR 0.152.
 */
package org.mule.modules.openair.utils;

import org.mule.modules.openair.exception.OpenAirException;

public class TransformationException
extends OpenAirException {
    public TransformationException(Throwable cause) {
        super("An error ocurred while doing the transformation of the payload.", cause);
    }
}
