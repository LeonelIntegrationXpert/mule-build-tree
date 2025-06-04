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

public class DeleteMetaData
extends AbstractMetaData {
    @Override
    public List<MetaDataKey> getMetadataKeys() throws WsdlDatasenseException {
        return this.getOperationMetaDataKeys("delete", this.getClass().getSimpleName());
    }

    @Override
    public MetaData getInputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        Map<String, MetaData> inputMetaData = this.getWsdlDatasenseParser().getInputMetaData(this.getOperation("delete"));
        return this.generateOaBaseObjectMetaData(inputMetaData.get("objects"), key, "oaBase");
    }

    @Override
    public MetaData getOutputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        Map<String, MetaData> outputMetaData = this.getWsdlDatasenseParser().getOutputMetaData(this.getOperation("delete"));
        return this.generateOaBaseObjectMetaData(outputMetaData.get("deleteReturn"), "oaError", "updateResult>>errors>>oaBase");
    }
}
