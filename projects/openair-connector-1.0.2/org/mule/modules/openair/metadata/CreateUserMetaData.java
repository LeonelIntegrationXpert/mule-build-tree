/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataKey
 */
package org.mule.modules.openair.metadata;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.modules.openair.metadata.AbstractMetaData;
import org.mule.modules.openair.utils.OpenAirMetaDataUtils;
import org.mule.modules.wsdl.openair.internal.datasense.WsdlDatasenseException;

public class CreateUserMetaData
extends AbstractMetaData {
    @Override
    public List<MetaDataKey> getMetadataKeys() throws WsdlDatasenseException {
        return this.getOperationMetaDataKey("createuser", "createUser", "Create User", this.getClass().getSimpleName());
    }

    @Override
    public MetaData getInputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        Map<String, MetaData> inputMetaData = this.getWsdlDatasenseParser().getInputMetaData(this.getOperation("createuser"));
        HashMap newInputMetaData = Maps.newHashMap();
        newInputMetaData.put("company", this.addOaAttributeOaSwitchToMetaDataObject(inputMetaData.get("company")));
        newInputMetaData.put("user", this.addOaAttributeOaSwitchToMetaDataObject(inputMetaData.get("user")));
        return OpenAirMetaDataUtils.combineMetaDataObjectsComplex("createUserRequest", newInputMetaData);
    }

    @Override
    public MetaData getOutputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        Map<String, MetaData> outputMetaData = this.getWsdlDatasenseParser().getOutputMetaData(this.getOperation("createuser"));
        return this.generateOaBaseObjectMetaData(outputMetaData.get("return"), "oaError", "errors>>oaBase");
    }
}
