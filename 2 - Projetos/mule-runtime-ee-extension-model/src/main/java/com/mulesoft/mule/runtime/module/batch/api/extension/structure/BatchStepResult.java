/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.annotation.NoImplement
 */
package com.mulesoft.mule.runtime.module.batch.api.extension.structure;

import com.mulesoft.mule.runtime.module.batch.api.extension.structure.BatchStepExceptionSummary;
import java.io.Serializable;
import org.mule.api.annotation.NoImplement;

@NoImplement
public interface BatchStepResult
extends Serializable {
    public long getReceivedRecords();

    public long getSuccessfulRecords();

    public long getFailedRecords();

    public BatchStepExceptionSummary getExceptionSummary();
}
