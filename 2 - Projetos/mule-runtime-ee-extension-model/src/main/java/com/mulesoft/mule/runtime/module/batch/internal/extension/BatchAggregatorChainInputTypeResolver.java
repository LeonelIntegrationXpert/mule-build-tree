/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.metadata.api.model.ArrayType
 *  org.mule.metadata.api.model.MetadataType
 *  org.mule.metadata.message.api.MessageMetadataType
 *  org.mule.metadata.message.api.MessageMetadataTypeBuilder
 *  org.mule.sdk.api.metadata.ChainInputMetadataContext
 *  org.mule.sdk.api.metadata.resolving.ChainInputTypeResolver
 */
package com.mulesoft.mule.runtime.module.batch.internal.extension;

import java.util.function.Function;
import org.mule.metadata.api.model.ArrayType;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.message.api.MessageMetadataType;
import org.mule.metadata.message.api.MessageMetadataTypeBuilder;
import org.mule.sdk.api.metadata.ChainInputMetadataContext;
import org.mule.sdk.api.metadata.resolving.ChainInputTypeResolver;

public class BatchAggregatorChainInputTypeResolver
implements ChainInputTypeResolver {
    public String getCategoryName() {
        return "BATCH_AGGREGATOR";
    }

    public String getResolverName() {
        return "BATCH_AGGREGATOR_CHAIN_INPUT";
    }

    public MessageMetadataType getChainInputMetadataType(ChainInputMetadataContext context) {
        MessageMetadataType messageMetadataType = context.getInputMessageMetadataType();
        MessageMetadataTypeBuilder chainMessageMetadataTypeBuilder = MessageMetadataType.builder();
        chainMessageMetadataTypeBuilder.attributes((MetadataType)context.getTypeBuilder().voidType().build());
        messageMetadataType.getPayloadType().map(this.getAggregatorMapper(context)).ifPresent(arg_0 -> ((MessageMetadataTypeBuilder)chainMessageMetadataTypeBuilder).payload(arg_0));
        return chainMessageMetadataTypeBuilder.build();
    }

    private Function<MetadataType, ArrayType> getAggregatorMapper(ChainInputMetadataContext context) {
        return recordType -> context.getTypeBuilder().arrayType().of(recordType).build();
    }
}
