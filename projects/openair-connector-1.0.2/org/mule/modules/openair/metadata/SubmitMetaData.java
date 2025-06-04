/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataKey
 */
package org.mule.modules.openair.metadata;

import java.util.List;
import java.util.Map;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.modules.openair.metadata.AbstractMetaData;
import org.mule.modules.wsdl.openair.internal.datasense.WsdlDatasenseException;

public class SubmitMetaData
extends AbstractMetaData {
    @Override
    public List<MetaDataKey> getMetadataKeys() throws WsdlDatasenseException {
        return this.getOperationMetaDataKeys("submit", this.getClass().getSimpleName());
    }

    @Override
    public MetaData getInputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        Map<String, MetaData> inputMetaData = this.getWsdlDatasenseParser().getInputMetaData(this.getOperation("submit"));
        MetaData metaData = this.generateOaBaseObjectMetaData(inputMetaData.get("requests"), "oaApproval", "SubmitRequest>>approval>>oaBase");
        return this.generateOaBaseObjectMetaData(metaData, key, "SubmitRequest>>submit");
    }

    @Override
    public MetaData getOutputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        return this.generateOaBaseObjectMetaData(this.getWsdlDatasenseParser().getOutputMetaData(this.getOperation("submit")).get("submitReturn"), "oaError", "SubmitResult>>errors>>oaBase");
    }
}
