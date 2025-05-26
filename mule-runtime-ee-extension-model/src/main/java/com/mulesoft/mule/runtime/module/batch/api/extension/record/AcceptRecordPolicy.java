/*
 * Decompiled with CFR 0.152.
 */
package com.mulesoft.mule.runtime.module.batch.api.extension.record;

import com.mulesoft.mule.runtime.module.batch.api.extension.record.RecordWithErrors;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum AcceptRecordPolicy {
    ALL{

        @Override
        public <T> boolean accept(RecordWithErrors record) {
            return true;
        }
    }
    ,
    ONLY_FAILURES{

        @Override
        public <T> boolean accept(RecordWithErrors record) {
            return record.hasErrors();
        }
    }
    ,
    NO_FAILURES{

        @Override
        public <T> boolean accept(RecordWithErrors record) {
            return !record.hasErrors();
        }
    };


    public abstract <T> boolean accept(RecordWithErrors var1);
}
