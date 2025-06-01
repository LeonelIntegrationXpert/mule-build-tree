/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.annotation.NoImplement
 */
package com.mulesoft.mule.runtime.module.batch.api.extension.structure;

import java.io.Serializable;
import java.util.Map;
import org.mule.api.annotation.NoImplement;

@NoImplement
public interface BatchStepExceptionSummary
extends Serializable {
    public Map<Class<? extends Exception>, Long> getExceptionsCount();
}
