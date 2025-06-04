/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.metadata.DefaultMetaData
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.MetaDataModel
 */
package org.mule.modules.openair.metadata;

import java.util.List;
import java.util.Map;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.modules.openair.metadata.AbstractMetaData;
import org.mule.modules.openair.utils.OpenAirMetaDataUtils;
import org.mule.modules.wsdl.openair.internal.datasense.WsdlDatasenseException;

public class MakeurlMetaData
extends AbstractMetaData {
    @Override
    public List<MetaDataKey> getMetadataKeys() throws WsdlDatasenseException {
        return this.getOperationMetaDataKey("makeurl", "makeURL", "Make URL", this.getClass().getSimpleName());
    }

    @Override
    public MetaData getInputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        Map<String, MetaData> inputMetaData = this.getWsdlDatasenseParser().getInputMetaData(this.getOperation("makeurl"));
        return new DefaultMetaData((MetaDataModel)OpenAirMetaDataUtils.asOpenAirMetaDataModel(inputMetaData.get("requests").getPayload()));
    }

    @Override
    public MetaData getOutputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        return this.generateOaBaseObjectMetaData(this.getWsdlDatasenseParser().getOutputMetaData(this.getOperation("makeurl")).get("MakeURLReturn"), "oaError", "makeURLResult>>errors>>oaBase");
    }
}
