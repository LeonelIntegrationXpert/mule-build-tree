/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.mule.common.metadata.AbstractStructuredMetaDataModel
 *  org.mule.common.metadata.MetaDataField
 *  org.mule.common.metadata.MetaDataModelProperty
 *  org.mule.common.metadata.MetaDataModelVisitor
 *  org.mule.common.metadata.XmlMetaDataModel
 *  org.mule.common.metadata.datatype.DataType
 *  org.mule.common.metadata.property.TextBasedExampleMetaDataModelProperty
 */
package org.mule.modules.openair.metadata.model;

import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.mule.common.metadata.AbstractStructuredMetaDataModel;
import org.mule.common.metadata.MetaDataField;
import org.mule.common.metadata.MetaDataModelProperty;
import org.mule.common.metadata.MetaDataModelVisitor;
import org.mule.common.metadata.XmlMetaDataModel;
import org.mule.common.metadata.datatype.DataType;
import org.mule.common.metadata.property.TextBasedExampleMetaDataModelProperty;
import org.mule.modules.openair.metadata.model.visitor.OpenAirMetadataModelVisitor;

public class OpenAirMetadataModel
extends AbstractStructuredMetaDataModel
implements XmlMetaDataModel {
    private final List<MetaDataField> openairFields;
    private QName rootElement;

    protected OpenAirMetadataModel(DataType dataType, List<MetaDataField> fields) {
        super(dataType, fields);
        this.openairFields = fields;
    }

    public OpenAirMetadataModel(DataType dataType, List<MetaDataField> fields, QName rootElement) {
        this(dataType, fields);
        this.rootElement = rootElement;
    }

    public OpenAirMetadataModel(DataType dataType, MetaDataField field, QName rootElement) {
        this(dataType, new ArrayList<MetaDataField>(Collections.singletonList(field)), rootElement);
    }

    public OpenAirMetadataModel(DataType dataType, XmlMetaDataModel xmlMetaDataModel, QName rootElement) {
        this(dataType, Lists.newArrayList((Iterable)xmlMetaDataModel.getFields()), rootElement);
    }

    @Deprecated
    public void accept(MetaDataModelVisitor modelVisitor) {
        ((OpenAirMetadataModelVisitor)OpenAirMetadataModelVisitor.class.cast(modelVisitor)).visitOpenAirMetaDataModelVisitor(this);
    }

    public List<InputStream> getSchemas() {
        return Collections.emptyList();
    }

    public String getExample() {
        return this.hasProperty(TextBasedExampleMetaDataModelProperty.class) ? ((TextBasedExampleMetaDataModelProperty)this.getProperty(TextBasedExampleMetaDataModelProperty.class)).getExampleContent() : null;
    }

    public void setExample(String xmlExample) {
        this.addProperty((MetaDataModelProperty)new TextBasedExampleMetaDataModelProperty(xmlExample));
    }

    public QName getRootElement() {
        return this.rootElement;
    }

    public List<MetaDataField> getFields() {
        return this.openairFields;
    }

    public MetaDataField getFieldByName(String name) {
        for (MetaDataField field : this.getFields()) {
            if (!StringUtils.equals((CharSequence)name, (CharSequence)field.getName())) continue;
            return field;
        }
        return null;
    }
}
