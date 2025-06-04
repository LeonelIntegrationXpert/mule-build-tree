/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.wsdl.Definition
 *  javax.wsdl.extensions.schema.Schema
 *  org.apache.commons.lang.StringUtils
 *  org.apache.commons.lang.WordUtils
 *  org.mule.common.metadata.DefaultMetaDataField
 *  org.mule.common.metadata.DefaultMetaDataKey
 *  org.mule.common.metadata.DefaultSimpleMetaDataModel
 *  org.mule.common.metadata.MetaDataField
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.datatype.DataType
 */
package org.mule.modules.openair.metadata.wsdlparser;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.mule.common.metadata.DefaultMetaDataField;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.DefaultSimpleMetaDataModel;
import org.mule.common.metadata.MetaDataField;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.datatype.DataType;
import org.mule.modules.openair.metadata.wsdlparser.node.NodeIterable;
import org.mule.modules.openair.utils.OpenAirConnectorUtils;
import org.mule.modules.openair.utils.OpenAirMetaDataUtils;
import org.mule.modules.wsdl.openair.internal.datasense.WsdlDatasenseException;
import org.mule.modules.wsdl.openair.internal.datasense.WsdlDatasenseParser;
import org.w3c.dom.Node;

public class OpenAirWsdlDatasenseParser
extends WsdlDatasenseParser {
    public List<MetaDataKey> parseOpenAirObjects(Definition wsdlDefinition, String operationName, String category) throws WsdlDatasenseException {
        ArrayList oaObjectMetaDataKeys = Lists.newArrayList();
        for (Node currentElement : new NodeIterable(((Schema)Schema.class.cast(wsdlDefinition.getTypes().getExtensibilityElements().get(0))).getElement().getChildNodes())) {
            if (currentElement.getNodeType() != 1 || currentElement.getLocalName().equalsIgnoreCase("complexType") || !currentElement.hasAttributes() || !StringUtils.startsWith((String)currentElement.getAttributes().getNamedItem("name").getNodeValue(), (String)"oa") || currentElement.getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase("oaBase")) continue;
            String id = operationName + "||" + currentElement.getAttributes().getNamedItem("name").getNodeValue();
            String displayName = this.friendlyName(currentElement.getAttributes().getNamedItem("name").getNodeValue());
            DefaultMetaDataKey metaDataKey = new DefaultMetaDataKey(id, displayName);
            metaDataKey.setCategory(category);
            oaObjectMetaDataKeys.add(metaDataKey);
        }
        return oaObjectMetaDataKeys;
    }

    public List<MetaDataField> getoaObjectFields(Definition wsdlDefinition, String oaBaseObject) throws WsdlDatasenseException {
        ArrayList<MetaDataField> fieldList = new ArrayList<MetaDataField>();
        for (Node currentElement : new NodeIterable(((Schema)Schema.class.cast(wsdlDefinition.getTypes().getExtensibilityElements().get(0))).getElement().getChildNodes())) {
            if (currentElement.getNodeType() != 1 || !currentElement.getLocalName().equalsIgnoreCase("complexType") || !currentElement.hasAttributes() || !StringUtils.equals((String)currentElement.getAttributes().getNamedItem("name").getNodeValue(), (String)oaBaseObject) || !currentElement.hasChildNodes()) continue;
            ArrayList<MetaDataField> combinedFields = new ArrayList<MetaDataField>();
            for (Node currentComplexType : new NodeIterable(currentElement.getChildNodes())) {
                combinedFields.addAll(this.handleComplexType(currentComplexType));
            }
            fieldList = combinedFields;
        }
        return fieldList;
    }

    private List<MetaDataField> handleComplexType(Node currentComplexType) {
        ArrayList<MetaDataField> result = new ArrayList<MetaDataField>();
        if (currentComplexType.getNodeType() == 1) {
            if (currentComplexType.getLocalName().equalsIgnoreCase("element")) {
                if (currentComplexType.hasAttributes()) {
                    String fieldName = currentComplexType.getAttributes().getNamedItem("name").getNodeValue();
                    String fieldType = OpenAirConnectorUtils.splitString(currentComplexType.getAttributes().getNamedItem("type").getNodeValue(), ":", 1);
                    if (fieldType.equalsIgnoreCase("ArrayOfoaBase")) {
                        result.add(OpenAirMetaDataUtils.createFieldWithOaBaseField(fieldName, new QName("ArrayOfoaBase")));
                    } else {
                        DataType returnType = DataType.UNKNOWN;
                        switch (fieldType.toLowerCase()) {
                            case "string": {
                                returnType = DataType.STRING;
                                break;
                            }
                            case "arrayofoabase": {
                                returnType = DataType.LIST;
                            }
                        }
                        result.add((MetaDataField)new DefaultMetaDataField(fieldName, (MetaDataModel)new DefaultSimpleMetaDataModel(returnType)));
                    }
                }
            } else {
                for (Node childNode : new NodeIterable(currentComplexType.getChildNodes())) {
                    result.addAll(this.handleComplexType(childNode));
                }
            }
        }
        return result;
    }

    private String friendlyName(String nodeValue) {
        String temp = nodeValue;
        temp = temp.substring(2);
        temp = temp.replaceAll("([a-z])([A-Z])", "$1 $2");
        temp = WordUtils.capitalize((String)temp, (char[])new char[]{'_'});
        temp = temp.replace('_', ' ');
        return temp;
    }
}
