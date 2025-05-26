/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.runtime.api.meta.model.stereotype.StereotypeModel
 *  org.mule.runtime.api.meta.model.stereotype.StereotypeModelBuilder
 *  org.mule.runtime.extension.api.stereotype.StereotypeDefinition
 */
package com.mulesoft.mule.runtime.module.batch.api.extension.stereotype;

import com.mulesoft.mule.runtime.module.batch.api.extension.stereotype.BatchOnCompleteStereotype;
import com.mulesoft.mule.runtime.module.batch.api.extension.stereotype.BatchProcessRecordsStereotype;
import com.mulesoft.mule.runtime.module.batch.api.extension.stereotype.BatchStepAggregatorStereotype;
import org.mule.runtime.api.meta.model.stereotype.StereotypeModel;
import org.mule.runtime.api.meta.model.stereotype.StereotypeModelBuilder;
import org.mule.runtime.extension.api.stereotype.StereotypeDefinition;

public class MuleBatchStereotypes {
    private static final String STEREOTYPE_NAMESPACE = "batch".toUpperCase();
    public static final StereotypeDefinition PROCESS_RECORDS_DEFINITION = new BatchProcessRecordsStereotype();
    public static final StereotypeDefinition STEP_AGGREGATOR_DEFINITION = new BatchStepAggregatorStereotype();
    public static final StereotypeDefinition ON_COMPLETE_DEFINITION = new BatchOnCompleteStereotype();
    public static final StereotypeDefinition HISTORY_DEFINITION = new BatchProcessRecordsStereotype();
    public static final StereotypeModel PROCESS_RECORDS = StereotypeModelBuilder.newStereotype((String)PROCESS_RECORDS_DEFINITION.getName(), (String)STEREOTYPE_NAMESPACE).build();
    public static final StereotypeModel STEP_AGGREGATOR = StereotypeModelBuilder.newStereotype((String)STEP_AGGREGATOR_DEFINITION.getName(), (String)STEREOTYPE_NAMESPACE).build();
    public static final StereotypeModel ON_COMPLETE = StereotypeModelBuilder.newStereotype((String)ON_COMPLETE_DEFINITION.getName(), (String)STEREOTYPE_NAMESPACE).build();
    public static final StereotypeModel HISTORY = StereotypeModelBuilder.newStereotype((String)HISTORY_DEFINITION.getName(), (String)STEREOTYPE_NAMESPACE).build();
}
