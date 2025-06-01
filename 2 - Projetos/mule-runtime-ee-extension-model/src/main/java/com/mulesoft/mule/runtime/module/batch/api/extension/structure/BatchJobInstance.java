/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.annotation.NoImplement
 */
package com.mulesoft.mule.runtime.module.batch.api.extension.structure;

import com.mulesoft.mule.runtime.module.batch.api.extension.structure.BatchJobInstanceStatus;
import com.mulesoft.mule.runtime.module.batch.api.extension.structure.BatchJobResult;
import java.io.Serializable;
import java.util.Date;
import org.mule.api.annotation.NoImplement;

@NoImplement
public interface BatchJobInstance
extends Serializable {
    public String getId();

    public BatchJobResult getResult();

    public BatchJobInstanceStatus getStatus();

    public long getRecordCount();

    public String getOwnerJobName();

    public Date getCreationTime();
}
