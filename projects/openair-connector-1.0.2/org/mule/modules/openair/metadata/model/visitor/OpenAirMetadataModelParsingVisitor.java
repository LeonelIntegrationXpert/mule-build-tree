/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.metadata.ListMetaDataModel
 *  org.mule.common.metadata.MetaDataModelVisitor
 *  org.mule.common.metadata.XmlMetaDataModel
 *  org.mule.common.metadata.datatype.DataType
 */
package org.mule.modules.openair.metadata.model.visitor;

import org.mule.common.metadata.ListMetaDataModel;
import org.mule.common.metadata.MetaDataModelVisitor;
import org.mule.common.metadata.XmlMetaDataModel;
import org.mule.common.metadata.datatype.DataType;
import org.mule.modules.openair.metadata.model.OpenAirMetadataModel;
import org.mule.modules.openair.metadata.model.visitor.DefaultOpenAirMetadataModelVisitor;

public class OpenAirMetadataModelParsingVisitor
extends DefaultOpenAirMetadataModelVisitor {
    private OpenAirMetadataModel model;

    @Override
    public void visitXmlMetaDataModel(XmlMetaDataModel xmlMetaDataModel) {
        this.model = new OpenAirMetadataModel(DataType.XML, xmlMetaDataModel, xmlMetaDataModel.getRootElement());
    }

    @Override
    public void visitListMetaDataModel(ListMetaDataModel listMetaDataModel) {
        listMetaDataModel.getElementModel().accept((MetaDataModelVisitor)this);
    }

    @Override
    public void visitOpenAirMetaDataModelVisitor(OpenAirMetadataModel openAirMetaDataModel) {
        this.model = openAirMetaDataModel;
    }

    public OpenAirMetadataModel getModel() {
        return this.model;
    }
}
