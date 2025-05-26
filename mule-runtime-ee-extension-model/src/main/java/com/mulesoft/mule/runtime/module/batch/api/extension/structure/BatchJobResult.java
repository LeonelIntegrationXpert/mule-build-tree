/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.annotation.NoImplement
 */
package com.mulesoft.mule.runtime.module.batch.api.extension.structure;

import com.mulesoft.mule.runtime.module.batch.api.extension.structure.BatchStepResult;
import java.io.Serializable;
import org.mule.api.annotation.NoImplement;

@NoImplement
public interface BatchJobResult
extends Serializable {
    public BatchStepResult getResultForStep(String var1);

    public long getElapsedTimeInMillis();

    public long getSuccessfulRecords();

    public long getFailedRecords();

    public long getTotalRecords();

    public long getLoadedRecords();

    public long getProcessedRecords();

    public String getBatchJobInstanceId();

    public boolean isFailedOnInputPhase();

    public Exception getInputPhaseException();

    public boolean isFailedOnLoadingPhase();

    public Exception getLoadingPhaseException();

    public boolean isFailedOnCompletePhase();

    public Exception getOnCompletePhaseException();
}
