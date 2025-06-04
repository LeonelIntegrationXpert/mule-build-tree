/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.mule.common.metadata.DefaultListMetaDataModel
 *  org.mule.common.metadata.DefaultMetaDataField
 *  org.mule.common.metadata.ListMetaDataModel
 *  org.mule.common.metadata.MetaDataField
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.MetaDataModelVisitor
 *  org.mule.common.metadata.datatype.DataType
 */
package org.mule.modules.openair.metadata.model.visitor;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mule.common.metadata.DefaultListMetaDataModel;
import org.mule.common.metadata.DefaultMetaDataField;
import org.mule.common.metadata.ListMetaDataModel;
import org.mule.common.metadata.MetaDataField;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.MetaDataModelVisitor;
import org.mule.common.metadata.datatype.DataType;
import org.mule.modules.openair.metadata.model.OpenAirMetadataModel;
import org.mule.modules.openair.metadata.model.visitor.DefaultOpenAirMetadataModelVisitor;
import org.mule.modules.openair.metadata.model.visitor.MaxLevelSearchMetadataVisitor;
import org.mule.modules.openair.metadata.model.visitor.SearchMetadataVisitor;
import org.mule.modules.openair.utils.OpenAirConnectorUtils;
import org.mule.modules.openair.utils.OpenAirMetaDataUtils;

public class DefaultSearchMetadataVisitor
extends DefaultOpenAirMetadataModelVisitor
implements SearchMetadataVisitor {
    private final String fieldLocation;
    private final int currentLevel;
    private final int maxLevels;
    private final DefaultMetaDataField wrappedOaObject;
    private MetaDataModel model;

    public DefaultSearchMetadataVisitor(String fieldLocation, int currentLevel, int maxLevels, DefaultMetaDataField wrappedOaObject) {
        this.fieldLocation = fieldLocation;
        this.currentLevel = currentLevel;
        this.maxLevels = maxLevels;
        this.wrappedOaObject = wrappedOaObject;
    }

    @Override
    public void visitListMetaDataModel(ListMetaDataModel listMetaDataModel) {
        this.model = new DefaultListMetaDataModel(this.model);
    }

    @Override
    public void defaultBehavior(MetaDataModel metaDataModel) {
        OpenAirMetadataModel openAirMetaDataModel = OpenAirMetaDataUtils.asOpenAirMetaDataModel(metaDataModel);
        List<MetaDataField> fields = openAirMetaDataModel.getFields();
        for (int index = 0; index < fields.size(); ++index) {
            if (!StringUtils.equals((CharSequence)fields.get(index).getName(), (CharSequence)OpenAirConnectorUtils.splitString(this.fieldLocation, ">>", this.currentLevel))) continue;
            MetaDataField currentField = fields.get(index);
            SearchMetadataVisitor visitor = (SearchMetadataVisitor)((Object)(this.currentLevel == this.maxLevels ? new MaxLevelSearchMetadataVisitor(this.wrappedOaObject) : new DefaultSearchMetadataVisitor(this.fieldLocation, this.currentLevel + 1, this.maxLevels, this.wrappedOaObject)));
            currentField.getMetaDataModel().accept((MetaDataModelVisitor)visitor);
            fields.set(index, (MetaDataField)new DefaultMetaDataField(currentField.getName(), visitor.getModel()));
        }
        this.model = new OpenAirMetadataModel(DataType.XML, fields, openAirMetaDataModel.getRootElement());
    }

    @Override
    public MetaDataModel getModel() {
        return this.model;
    }
}
