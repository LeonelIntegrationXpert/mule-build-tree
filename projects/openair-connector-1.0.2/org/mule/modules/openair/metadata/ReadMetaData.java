/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataKey
 */
package org.mule.modules.openair.metadata;

import java.util.List;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.modules.openair.metadata.AbstractMetaData;
import org.mule.modules.wsdl.openair.internal.datasense.WsdlDatasenseException;

public class ReadMetaData
extends AbstractMetaData {
    @Override
    public List<MetaDataKey> getMetadataKeys() throws WsdlDatasenseException {
        return this.getOperationMetaDataKeys("read", this.getClass().getSimpleName());
    }

    @Override
    public MetaData getInputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        return this.generateOaBaseObjectMetaData(this.getWsdlDatasenseParser().getInputMetaData(this.getOperation("read")).get("method"), key, "ReadRequest>>objects>>oaBase");
    }

    @Override
    public MetaData getOutputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        return this.generateOaBaseObjectMetaData(this.generateOaBaseObjectMetaData(this.getWsdlDatasenseParser().getOutputMetaData(this.getOperation("read")).get("readReturn"), key, "ReadResult>>objects>>oaBase"), "oaError", "ReadResult>>errors>>oaBase");
    }
}
