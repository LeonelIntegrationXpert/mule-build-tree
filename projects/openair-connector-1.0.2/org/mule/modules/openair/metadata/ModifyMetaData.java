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
import org.mule.modules.openair.utils.OpenAirMetaDataUtils;
import org.mule.modules.wsdl.openair.internal.datasense.WsdlDatasenseException;

public class ModifyMetaData
extends AbstractMetaData {
    @Override
    public List<MetaDataKey> getMetadataKeys() throws WsdlDatasenseException {
        return this.getOperationMetaDataKeys("modify", this.getClass().getSimpleName());
    }

    @Override
    public MetaData getInputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        return this.generateOaBaseObjectMetaData(OpenAirMetaDataUtils.combineMetaDataObjectsComplex("modifyRequest", this.getWsdlDatasenseParser().getInputMetaData(this.getOperation("modify"))), key, "ArrayOfoaBase>>oaBase");
    }

    @Override
    public MetaData getOutputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        return this.generateOaBaseObjectMetaData(this.getWsdlDatasenseParser().getOutputMetaData(this.getOperation("modify")).get("modifyReturn"), "oaError", "updateResult>>errors>>oaBase");
    }
}
