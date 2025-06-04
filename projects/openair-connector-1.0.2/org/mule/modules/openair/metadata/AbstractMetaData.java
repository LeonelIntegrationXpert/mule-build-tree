/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 *  javax.wsdl.Definition
 *  org.mule.common.metadata.DefaultMetaData
 *  org.mule.common.metadata.DefaultMetaDataField
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataField
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.datatype.DataType
 */
package org.mule.modules.openair.metadata;

import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.wsdl.Definition;
import javax.xml.namespace.QName;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultMetaDataField;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataField;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.datatype.DataType;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKey;
import org.mule.modules.openair.OpenAirConnector;
import org.mule.modules.openair.metadata.model.OpenAirMetadataModel;
import org.mule.modules.openair.metadata.wsdlparser.OpenAirWsdlDatasenseParser;
import org.mule.modules.openair.utils.OpenAirConnectorUtils;
import org.mule.modules.openair.utils.OpenAirMetaDataUtils;
import org.mule.modules.wsdl.openair.internal.datasense.WsdlDatasenseException;

public abstract class AbstractMetaData {
    @Inject
    private OpenAirConnector connector;

    public OpenAirConnector getConnector() {
        return this.connector;
    }

    public void setConnector(OpenAirConnector connector) {
        this.connector = connector;
    }

    public Definition getWsdlDefinition() {
        return this.getConnector().getConfig().getWsdlDefinition();
    }

    protected List<MetaDataKey> getOperationMetaDataKeys(String operationName, String categoryName) throws WsdlDatasenseException {
        return this.getWsdlDatasenseParser().parseOpenAirObjects(this.getWsdlDefinition(), this.getOperation(operationName).getId(), categoryName);
    }

    protected List<MetaDataKey> getOperationMetaDataKey(String operationName, String objectID, String objectDisplayName, String categoryName) {
        String metadataKeyID = this.getOperation(operationName).getId() + "||" + objectID;
        return Collections.singletonList(OpenAirMetaDataUtils.createMetaDataKey(metadataKeyID, objectDisplayName, categoryName));
    }

    protected ComposedMetaDataKey getOperation(String operationName) {
        return this.getConnector().getConfig().getOperationMetadata().get(operationName);
    }

    protected MetaData generateOaBaseObjectMetaData(MetaData metadata, MetaDataKey metadataKey, String fieldLocation) throws WsdlDatasenseException {
        return this.generateOaBaseObjectMetaData(metadata, OpenAirConnectorUtils.splitString(metadataKey.getId(), "||"), fieldLocation);
    }

    protected MetaData generateOaBaseObjectMetaData(MetaData metadata, String selectedOaBaseObject, String fieldLocation) throws WsdlDatasenseException {
        Definition wsdlDefinition = this.getWsdlDefinition();
        OpenAirMetadataModel metaDataModelOpenAir = OpenAirMetaDataUtils.asOpenAirMetaDataModel(metadata.getPayload());
        List<MetaDataField> oaObjectCombinedFields = this.getWsdlDatasenseParser().getoaObjectFields(wsdlDefinition, selectedOaBaseObject);
        this.addOaAttributeOaSwitchToOaObject(oaObjectCombinedFields, metaDataModelOpenAir.getRootElement());
        DefaultMetaDataField wrappedOaObject = (DefaultMetaDataField)OpenAirMetaDataUtils.wrapOaBaseObject(selectedOaBaseObject, oaObjectCombinedFields, metaDataModelOpenAir.getRootElement());
        OpenAirMetadataModel openAirFieldWithOaObject = OpenAirMetaDataUtils.addOaObjectToField(metaDataModelOpenAir, fieldLocation, wrappedOaObject);
        return new DefaultMetaData((MetaDataModel)new OpenAirMetadataModel(DataType.XML, openAirFieldWithOaObject.getFields(), metaDataModelOpenAir.getRootElement()));
    }

    public MetaData addOaAttributeOaSwitchToMetaDataObject(MetaData metadata) throws WsdlDatasenseException {
        OpenAirMetadataModel asOpenAirMetaDataModel = OpenAirMetaDataUtils.asOpenAirMetaDataModel(metadata.getPayload());
        this.addOaAttributeOaSwitchToOaObject(asOpenAirMetaDataModel.getFields(), asOpenAirMetaDataModel.getRootElement());
        return new DefaultMetaData((MetaDataModel)asOpenAirMetaDataModel);
    }

    protected void addOaAttributeOaSwitchToOaObject(List<MetaDataField> oaObjectFields, QName rootElement) throws WsdlDatasenseException {
        Definition wsdlDefinition = this.getWsdlDefinition();
        List<MetaDataField> oaAttributeFields = this.getWsdlDatasenseParser().getoaObjectFields(wsdlDefinition, "oaAttribute");
        DefaultMetaDataField oaAttributeWrappedOaObject = (DefaultMetaDataField)OpenAirMetaDataUtils.wrapOaBaseObject("oaAttribute", oaAttributeFields, rootElement);
        OpenAirMetaDataUtils.addOaObjectToField(oaObjectFields, rootElement, "attributes>>oaBase", oaAttributeWrappedOaObject);
        List<MetaDataField> oaSwitchFields = this.getWsdlDatasenseParser().getoaObjectFields(wsdlDefinition, "oaSwitch");
        DefaultMetaDataField oaSwitchWrappedOaObject = (DefaultMetaDataField)OpenAirMetaDataUtils.wrapOaBaseObject("oaSwitch", oaSwitchFields, rootElement);
        OpenAirMetaDataUtils.addOaObjectToField(oaObjectFields, rootElement, "flags>>oaBase", oaSwitchWrappedOaObject);
    }

    protected OpenAirWsdlDatasenseParser getWsdlDatasenseParser() {
        return this.getConnector().getConfig().getWsdlDatasenseParser();
    }

    public abstract List<MetaDataKey> getMetadataKeys() throws WsdlDatasenseException;

    public abstract MetaData getInputMetadata(MetaDataKey var1) throws WsdlDatasenseException;

    public abstract MetaData getOutputMetadata(MetaDataKey var1) throws WsdlDatasenseException;
}
