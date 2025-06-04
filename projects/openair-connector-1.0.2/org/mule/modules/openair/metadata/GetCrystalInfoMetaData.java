/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.metadata.DefaultMetaData
 *  org.mule.common.metadata.DefaultUnknownMetaDataModel
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.MetaDataModel
 */
package org.mule.modules.openair.metadata;

import java.util.List;
import java.util.Map;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultUnknownMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.modules.openair.metadata.AbstractMetaData;
import org.mule.modules.openair.utils.OpenAirMetaDataUtils;
import org.mule.modules.wsdl.openair.internal.datasense.WsdlDatasenseException;

public class GetCrystalInfoMetaData
extends AbstractMetaData {
    @Override
    public List<MetaDataKey> getMetadataKeys() throws WsdlDatasenseException {
        return this.getOperationMetaDataKey("getcrystalinfo", "crystalInfo", "Crystal Info", this.getClass().getSimpleName());
    }

    @Override
    public MetaData getInputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        return new DefaultMetaData((MetaDataModel)new DefaultUnknownMetaDataModel());
    }

    @Override
    public MetaData getOutputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        Map<String, MetaData> outputMetaData = this.getWsdlDatasenseParser().getOutputMetaData(this.getOperation("getcrystalinfo"));
        return new DefaultMetaData((MetaDataModel)OpenAirMetaDataUtils.asOpenAirMetaDataModel(outputMetaData.get("getCrystalInfoReturn").getPayload()));
    }
}
