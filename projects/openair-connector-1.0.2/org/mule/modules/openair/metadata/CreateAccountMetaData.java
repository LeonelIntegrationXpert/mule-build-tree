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

public class CreateAccountMetaData
extends AbstractMetaData {
    @Override
    public List<MetaDataKey> getMetadataKeys() throws WsdlDatasenseException {
        return this.getOperationMetaDataKey("createaccount", "createAccount", "Create Account", this.getClass().getSimpleName());
    }

    @Override
    public MetaData getInputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        Map<String, MetaData> inputMetaData = this.getWsdlDatasenseParser().getInputMetaData(this.getOperation("createaccount"));
        HashMap newInputMetaData = Maps.newHashMap();
        newInputMetaData.put("company", this.addOaAttributeOaSwitchToMetaDataObject(inputMetaData.get("company")));
        newInputMetaData.put("user", this.addOaAttributeOaSwitchToMetaDataObject(inputMetaData.get("user")));
        return OpenAirMetaDataUtils.combineMetaDataObjectsComplex("createAccountRequest", newInputMetaData);
    }

    @Override
    public MetaData getOutputMetadata(MetaDataKey key) throws WsdlDatasenseException {
        return this.generateOaBaseObjectMetaData(this.getWsdlDatasenseParser().getOutputMetaData(this.getOperation("createaccount")).get("return"), "oaError", "errors>>oaBase");
    }
}
