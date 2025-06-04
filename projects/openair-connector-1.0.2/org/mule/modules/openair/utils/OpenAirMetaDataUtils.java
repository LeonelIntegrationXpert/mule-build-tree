/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.mule.common.metadata.DefaultListMetaDataModel
 *  org.mule.common.metadata.DefaultMetaData
 *  org.mule.common.metadata.DefaultMetaDataField
 *  org.mule.common.metadata.DefaultMetaDataKey
 *  org.mule.common.metadata.DefaultSimpleMetaDataModel
 *  org.mule.common.metadata.DefaultXmlMetaDataModel
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataField
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.MetaDataModelVisitor
 *  org.mule.common.metadata.datatype.DataType
 */
package org.mule.modules.openair.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.mule.common.metadata.DefaultListMetaDataModel;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultMetaDataField;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.DefaultSimpleMetaDataModel;
import org.mule.common.metadata.DefaultXmlMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataField;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.MetaDataModelVisitor;
import org.mule.common.metadata.datatype.DataType;
import org.mule.modules.openair.metadata.model.OpenAirMetadataModel;
import org.mule.modules.openair.metadata.model.visitor.DefaultOpenAirMetadataModelVisitor;
import org.mule.modules.openair.metadata.model.visitor.DefaultSearchMetadataVisitor;
import org.mule.modules.openair.metadata.model.visitor.MaxLevelSearchMetadataVisitor;
import org.mule.modules.openair.metadata.model.visitor.OpenAirMetadataModelParsingVisitor;
import org.mule.modules.openair.metadata.model.visitor.SearchMetadataVisitor;
import org.mule.modules.openair.utils.OpenAirConnectorUtils;

public final class OpenAirMetaDataUtils {
    public static MetaDataField createFieldWithOaBaseField(String fieldName, QName qname) {
        QName newQNameObject = new QName("http://namespaces.soaplite.com/perl", qname.getLocalPart(), "ns0");
        return new DefaultMetaDataField(fieldName, (MetaDataModel)new OpenAirMetadataModel(DataType.XML, (MetaDataField)new DefaultMetaDataField("oaBase", (MetaDataModel)new DefaultListMetaDataModel((MetaDataModel)new OpenAirMetadataModel(DataType.XML, new ArrayList<MetaDataField>(), newQNameObject))), newQNameObject));
    }

    public static OpenAirMetadataModel asOpenAirMetaDataModel(MetaDataModel metadata) {
        OpenAirMetadataModelParsingVisitor visitor = new OpenAirMetadataModelParsingVisitor();
        metadata.accept((MetaDataModelVisitor)visitor);
        return visitor.getModel();
    }

    public static MetaDataField wrapOaBaseObject(String oaObjectName, List<MetaDataField> fields, QName qNameObject) {
        return new DefaultMetaDataField(oaObjectName, (MetaDataModel)new OpenAirMetadataModel(DataType.XML, fields, qNameObject));
    }

    public static OpenAirMetadataModel addOaObjectToField(OpenAirMetadataModel openAirMetaDataModel, String fieldLocation, DefaultMetaDataField wrappedOaObject) {
        return OpenAirMetaDataUtils.addOaObjectToField(openAirMetaDataModel.getFields(), openAirMetaDataModel.getRootElement(), fieldLocation, wrappedOaObject);
    }

    public static OpenAirMetadataModel addOaObjectToField(List<MetaDataField> fields, QName rootElement, String fieldLocation, DefaultMetaDataField wrappedOaObject) {
        return new OpenAirMetadataModel(DataType.XML, OpenAirMetaDataUtils.search(fields, fieldLocation, 0, StringUtils.split((String)fieldLocation, (String)">>").length - 1, wrappedOaObject), rootElement);
    }

    private static List<MetaDataField> search(List<MetaDataField> listOfFields, String fieldLocation, int currentLevel, int maxLevels, DefaultMetaDataField wrappedOaObject) {
        ArrayList<MetaDataField> result = new ArrayList<MetaDataField>();
        for (MetaDataField field : listOfFields) {
            if (StringUtils.equals((CharSequence)field.getName(), (CharSequence)OpenAirConnectorUtils.splitString(fieldLocation, ">>", currentLevel))) {
                DefaultOpenAirMetadataModelVisitor defaultOpenAirMetadataModelVisitor;
                if (currentLevel == maxLevels) {
                    defaultOpenAirMetadataModelVisitor = new MaxLevelSearchMetadataVisitor(wrappedOaObject);
                } else {
                    ++currentLevel;
                    defaultOpenAirMetadataModelVisitor = new DefaultSearchMetadataVisitor(fieldLocation, currentLevel, maxLevels, wrappedOaObject);
                }
                SearchMetadataVisitor visitor = defaultOpenAirMetadataModelVisitor;
                field.getMetaDataModel().accept((MetaDataModelVisitor)visitor);
                result.add((MetaDataField)new DefaultMetaDataField(field.getName(), visitor.getModel()));
                continue;
            }
            result.add(field);
        }
        return result;
    }

    public static MetaData combineMetaDataObjectsSimple(String parentElement, Map<String, MetaData> metaDataMap) {
        Set<String> keySet = metaDataMap.keySet();
        ArrayList<MetaDataField> combinedFields = new ArrayList<MetaDataField>();
        for (String key : keySet) {
            MetaData value = metaDataMap.get(key);
            DefaultXmlMetaDataModel xmlMetaDataModel = (DefaultXmlMetaDataModel)value.getPayload();
            List fields = xmlMetaDataModel.getFields();
            DataType dataType = ((MetaDataField)fields.get(0)).getMetaDataModel().getDataType();
            combinedFields.add((MetaDataField)new DefaultMetaDataField(key, (MetaDataModel)new DefaultSimpleMetaDataModel(dataType)));
        }
        return OpenAirMetaDataUtils.wrapCombinedFieldsToModel(parentElement, combinedFields);
    }

    public static MetaData combineMetaDataObjectsComplex(String parentElement, Map<String, MetaData> metaDataMap) {
        ArrayList<MetaDataField> combinedFields = new ArrayList<MetaDataField>();
        for (Map.Entry<String, MetaData> metaDataObject : metaDataMap.entrySet()) {
            MetaDataField wrappedField = OpenAirMetaDataUtils.wrapField(metaDataObject.getValue());
            combinedFields.add(wrappedField);
        }
        return OpenAirMetaDataUtils.wrapCombinedFieldsToModel(parentElement, combinedFields);
    }

    private static MetaData wrapCombinedFieldsToModel(String parentElement, List<MetaDataField> combinedFields) {
        QName rootElement = new QName("http://namespaces.soaplite.com/perl", parentElement, "ns0");
        OpenAirMetadataModel combinedModel = new OpenAirMetadataModel(DataType.XML, combinedFields, rootElement);
        return new DefaultMetaData((MetaDataModel)combinedModel);
    }

    private static MetaDataField wrapField(MetaData value) {
        OpenAirMetadataModel newMetaDataModel = OpenAirMetaDataUtils.asOpenAirMetaDataModel(value.getPayload());
        return new DefaultMetaDataField(newMetaDataModel.getRootElement().getLocalPart(), (MetaDataModel)newMetaDataModel);
    }

    public static MetaDataKey createMetaDataKey(String id, String displayName, String category) {
        DefaultMetaDataKey metadataKey = new DefaultMetaDataKey(id, displayName);
        metadataKey.setCategory(category);
        return metadataKey;
    }

    private OpenAirMetaDataUtils() {
    }
}
