/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  javax.wsdl.Definition
 *  javax.wsdl.Message
 *  javax.wsdl.Part
 *  javax.wsdl.Port
 *  javax.wsdl.Service
 *  javax.wsdl.extensions.soap.SOAPHeader
 *  org.apache.commons.lang.StringUtils
 *  org.mule.common.DefaultResult
 *  org.mule.common.FailureType
 *  org.mule.common.Result
 *  org.mule.common.Result$Status
 *  org.mule.common.metadata.DefaultMetaData
 *  org.mule.common.metadata.DefaultMetaDataKey
 *  org.mule.common.metadata.DefaultSimpleMetaDataModel
 *  org.mule.common.metadata.DefaultUnknownMetaDataModel
 *  org.mule.common.metadata.DefaultXmlMetaDataModel
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataFailureType
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.MetaDataModelProperty
 *  org.mule.common.metadata.MetaDataPropertyScope
 *  org.mule.common.metadata.builder.DefaultMetaDataBuilder
 *  org.mule.common.metadata.builder.XmlMetaDataBuilder
 *  org.mule.common.metadata.datatype.DataType
 *  org.mule.common.metadata.field.property.MetaDataFieldProperty
 *  org.mule.common.metadata.key.property.TypeDescribingProperty
 *  org.mule.common.metadata.key.property.TypeDescribingProperty$TypeScope
 *  org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKeyBuilder$CombinationBuilder
 *  org.mule.devkit.3.9.0.internal.ws.metadata.utils.InvokeWsdlResolver$OperationMode
 */
package org.mule.devkit.3.9.0.internal.ws.metadata;

import com.google.common.base.Optional;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.xml.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.mule.common.DefaultResult;
import org.mule.common.FailureType;
import org.mule.common.Result;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.DefaultSimpleMetaDataModel;
import org.mule.common.metadata.DefaultUnknownMetaDataModel;
import org.mule.common.metadata.DefaultXmlMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataFailureType;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.MetaDataModelProperty;
import org.mule.common.metadata.MetaDataPropertyScope;
import org.mule.common.metadata.builder.DefaultMetaDataBuilder;
import org.mule.common.metadata.builder.XmlMetaDataBuilder;
import org.mule.common.metadata.datatype.DataType;
import org.mule.common.metadata.field.property.MetaDataFieldProperty;
import org.mule.common.metadata.key.property.TypeDescribingProperty;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKeyBuilder;
import org.mule.devkit.3.9.0.api.metadata.DefaultMetaDataKeyLevel;
import org.mule.devkit.3.9.0.api.metadata.MetaDataKeyLevel;
import org.mule.devkit.3.9.0.api.ws.definition.ServiceDefinition;
import org.mule.devkit.3.9.0.internal.metadata.InternalComposedMetaDataKeyBuilder;
import org.mule.devkit.3.9.0.internal.metadata.fixes.STUDIO7157;
import org.mule.devkit.3.9.0.internal.ws.common.EnhancedServiceDefinition;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlAdapter;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlSplitKey;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlUtils;
import org.mule.devkit.3.9.0.internal.ws.metadata.utils.InvokeWsdlResolver;
import org.mule.devkit.3.9.0.internal.ws.model.WsdlIntrospecterUtils;

public class WsdlMetaDataDescriptor {
    public static final String SOAP_PREFIX = "soap.";

    public Result<List<MetaDataKey>> getMetaDataKeys(WsdlAdapter wsdlAdapter) {
        try {
            ArrayList<MetaDataKey> gatheredMetaDataKeys = new ArrayList<MetaDataKey>();
            for (ServiceDefinition serviceDefinition : wsdlAdapter.serviceDefinitions()) {
                gatheredMetaDataKeys.addAll(this.getKeysFor(wsdlAdapter, serviceDefinition));
            }
            return new DefaultResult(gatheredMetaDataKeys, Result.Status.SUCCESS);
        }
        catch (Exception e) {
            return new DefaultResult(null, Result.Status.FAILURE, "There was an error retrieving the metadata keys from service provider after acquiring connection, for more detailed information please read the provided stacktrace", (FailureType)MetaDataFailureType.ERROR_METADATA_KEYS_RETRIEVER, (Throwable)e);
        }
    }

    public Result<MetaData> getMetaData(MetaDataKey metaDataKey, WsdlAdapter wsdlAdapter) {
        try {
            MetaData metaData;
            String name = metaDataKey.getId();
            boolean generatedStructure = false;
            EnhancedServiceDefinition enhancedServiceDefinition = this.resolveEnhancedServiceDefinition(metaDataKey, wsdlAdapter);
            if (this.isInputMetaData(metaDataKey)) {
                metaData = this.loadInputMetadata(enhancedServiceDefinition);
            } else {
                name = name + " Result";
                generatedStructure = true;
                metaData = this.loadOutputMetadata(enhancedServiceDefinition);
            }
            metaData.getPayload().addProperty((MetaDataModelProperty)STUDIO7157.getStructureIdentifierMetaDataModelProperty((MetaDataKey)new DefaultMetaDataKey(name, null), false, generatedStructure));
            return new DefaultResult((Object)metaData);
        }
        catch (Exception e) {
            return new DefaultResult(null, Result.Status.FAILURE, this.getMetaDataException(metaDataKey), (FailureType)MetaDataFailureType.ERROR_METADATA_RETRIEVER, (Throwable)e);
        }
    }

    private boolean isInputMetaData(MetaDataKey metaDataKey) {
        return TypeDescribingProperty.TypeScope.INPUT.equals((Object)((TypeDescribingProperty)metaDataKey.getProperty(TypeDescribingProperty.class)).getTypeScope());
    }

    private String getMetaDataException(MetaDataKey key) {
        if (!StringUtils.isBlank((String)key.getId())) {
            return "There was an error retrieving metadata from key: " + key.getId() + " after acquiring the connection, for more detailed information please read the provided stacktrace";
        }
        return "There was an error retrieving metadata after acquiring the connection, MetaDataKey is null or its id is null, for more detailed information please read the provided stacktrace";
    }

    private List<MetaDataKey> getKeysFor(WsdlAdapter wsdlAdapter, ServiceDefinition serviceDefinition) throws Exception {
        String keySeparator = wsdlAdapter.wsdlSeparator();
        Definition definition = WsdlUtils.parseWSDL(serviceDefinition.getWsdlUrl().toString());
        Service service = WsdlIntrospecterUtils.resolveService(serviceDefinition, definition);
        Port port = WsdlIntrospecterUtils.resolvePort(serviceDefinition, service, definition);
        DefaultMetaDataKeyLevel operations = new DefaultMetaDataKeyLevel();
        List<String> excludedOperations = serviceDefinition.getExcludedOperations();
        for (String operation : WsdlUtils.getOperationNames(port)) {
            if (excludedOperations.contains(operation)) continue;
            operations.addId(operation, operation);
        }
        ComposedMetaDataKeyBuilder.CombinationBuilder builder = InternalComposedMetaDataKeyBuilder.getInstance().withSeparator(keySeparator).newKeyCombination();
        if (!wsdlAdapter.singleServiceDefinitionId().isPresent()) {
            builder.newLevel().addId(serviceDefinition.getId(), serviceDefinition.getDisplayName()).endLevel();
        }
        return InternalComposedMetaDataKeyBuilder.toSimpleKey(builder.newLevel().addIds((MetaDataKeyLevel)operations).endLevel().endKeyCombination().build(), keySeparator);
    }

    private EnhancedServiceDefinition resolveEnhancedServiceDefinition(MetaDataKey key, WsdlAdapter wsdlAdapter) throws Exception {
        WsdlSplitKey splitKey = new WsdlSplitKey(key.getId(), wsdlAdapter);
        return wsdlAdapter.wsResolver().enhancedServiceDefinition(splitKey.id(), wsdlAdapter, splitKey.operation());
    }

    private MetaData loadInputMetadata(EnhancedServiceDefinition enhancedServiceDefinition) throws Exception {
        return this.loadMetadata(enhancedServiceDefinition, InvokeWsdlResolver.OperationMode.INPUT);
    }

    private MetaData loadOutputMetadata(EnhancedServiceDefinition enhancedServiceDefinition) throws Exception {
        return this.loadMetadata(enhancedServiceDefinition, InvokeWsdlResolver.OperationMode.OUTPUT);
    }

    private MetaData loadMetadata(EnhancedServiceDefinition enhancedServiceDefinition, InvokeWsdlResolver.OperationMode operationMode) throws Exception {
        MetaDataPropertyScope scope = operationMode == InvokeWsdlResolver.OperationMode.INPUT ? MetaDataPropertyScope.OUTBOUND : MetaDataPropertyScope.INBOUND;
        InvokeWsdlResolver invokeWsdlResolver = new InvokeWsdlResolver(operationMode, enhancedServiceDefinition.getWsdlUrl().toString(), enhancedServiceDefinition.getService(), enhancedServiceDefinition.getPort(), enhancedServiceDefinition.getOperation());
        MetaData outputMetadata = this.createMetaData(invokeWsdlResolver.getSchemas(), invokeWsdlResolver.getMessagePart(), enhancedServiceDefinition.getWsdlUrl());
        if (invokeWsdlResolver.getMessagePart().isPresent()) {
            this.addProperties(outputMetadata, invokeWsdlResolver, scope);
        }
        return outputMetadata;
    }

    private MetaData createMetaData(List<String> schemas, Optional<Part> partOptional, URL url) {
        if (partOptional.isPresent()) {
            Part part = (Part)partOptional.get();
            if (part.getElementName() != null) {
                QName elementName = part.getElementName();
                XmlMetaDataBuilder createXmlObject = new DefaultMetaDataBuilder().createXmlObject(elementName);
                for (String schema : schemas) {
                    createXmlObject.addSchemaStringList(new String[]{schema});
                }
                createXmlObject.setEncoding(this.charset());
                createXmlObject.setSourceUri(url);
                return new DefaultMetaData(createXmlObject.build());
            }
            if (part.getTypeName() != null) {
                DataType dataType = this.getDataTypeFromTypeName(part);
                DefaultSimpleMetaDataModel defaultSimpleMetaDataModel = new DefaultSimpleMetaDataModel(dataType);
                return new DefaultMetaData((MetaDataModel)defaultSimpleMetaDataModel);
            }
        }
        return new DefaultMetaData((MetaDataModel)new DefaultUnknownMetaDataModel());
    }

    private void addProperties(MetaData inputMetadata, InvokeWsdlResolver invokeWsdlResolver, MetaDataPropertyScope metaDataPropertyScope) {
        List<SOAPHeader> outputHeaders = invokeWsdlResolver.getOperationHeaders();
        for (SOAPHeader soapHeader : outputHeaders) {
            Message message = invokeWsdlResolver.getDefinition().getMessage(soapHeader.getMessage());
            if (message == null) continue;
            Part part = message.getPart(soapHeader.getPart());
            inputMetadata.addProperty(metaDataPropertyScope, SOAP_PREFIX + soapHeader.getPart(), (MetaDataModel)new DefaultXmlMetaDataModel(invokeWsdlResolver.getSchemas(), part.getElementName(), this.charset(), new MetaDataModelProperty[0]), new MetaDataFieldProperty[0]);
        }
    }

    private Charset charset() {
        return Charset.defaultCharset();
    }

    private DataType getDataTypeFromTypeName(Part part) {
        String localPart = part.getTypeName().getLocalPart();
        HashMap<String, DataType> types = new HashMap<String, DataType>();
        types.put("string", DataType.STRING);
        types.put("boolean", DataType.BOOLEAN);
        types.put("date", DataType.DATE);
        types.put("decimal", DataType.DECIMAL);
        types.put("byte", DataType.BYTE);
        types.put("unsignedByte", DataType.BYTE);
        types.put("dateTime", DataType.DATE_TIME);
        types.put("int", DataType.INTEGER);
        types.put("integer", DataType.INTEGER);
        types.put("unsignedInt", DataType.INTEGER);
        types.put("short", DataType.INTEGER);
        types.put("unsignedShort", DataType.INTEGER);
        types.put("long", DataType.LONG);
        types.put("unsignedLong", DataType.LONG);
        types.put("double", DataType.DOUBLE);
        DataType dataType = (DataType)types.get(localPart);
        return dataType != null ? dataType : DataType.STRING;
    }
}
