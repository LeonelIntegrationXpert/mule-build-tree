/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.metadata.DefaultListMetaDataModel
 *  org.mule.common.metadata.DefaultMetaDataField
 *  org.mule.common.metadata.ListMetaDataModel
 *  org.mule.common.metadata.MetaDataField
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.datatype.DataType
 */
package org.mule.modules.openair.metadata.model.visitor;

import org.mule.common.metadata.DefaultListMetaDataModel;
import org.mule.common.metadata.DefaultMetaDataField;
import org.mule.common.metadata.ListMetaDataModel;
import org.mule.common.metadata.MetaDataField;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.datatype.DataType;
import org.mule.modules.openair.metadata.model.OpenAirMetadataModel;
import org.mule.modules.openair.metadata.model.visitor.DefaultOpenAirMetadataModelVisitor;
import org.mule.modules.openair.metadata.model.visitor.SearchMetadataVisitor;
import org.mule.modules.openair.utils.OpenAirMetaDataUtils;

public class MaxLevelSearchMetadataVisitor
extends DefaultOpenAirMetadataModelVisitor
implements SearchMetadataVisitor {
    private final DefaultMetaDataField wrappedOaObject;
    private MetaDataModel model;

    public MaxLevelSearchMetadataVisitor(DefaultMetaDataField wrappedOaObject) {
        this.wrappedOaObject = wrappedOaObject;
    }

    @Override
    public void visitListMetaDataModel(ListMetaDataModel listMetaDataModel) {
        OpenAirMetadataModel openAirMetaDataModel = OpenAirMetaDataUtils.asOpenAirMetaDataModel(listMetaDataModel.getElementModel());
        openAirMetaDataModel.getFields().add((MetaDataField)this.wrappedOaObject);
        this.model = new DefaultListMetaDataModel((MetaDataModel)openAirMetaDataModel);
    }

    @Override
    public void defaultBehavior(MetaDataModel metaDataModel) {
        OpenAirMetadataModel openAirMetaDataModel = OpenAirMetaDataUtils.asOpenAirMetaDataModel(metaDataModel);
        openAirMetaDataModel.getFields().add((MetaDataField)this.wrappedOaObject);
        this.model = new OpenAirMetadataModel(DataType.XML, openAirMetaDataModel.getFields(), openAirMetaDataModel.getRootElement());
    }

    @Override
    public MetaDataModel getModel() {
        return this.model;
    }
}
