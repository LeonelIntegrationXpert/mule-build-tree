/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.metadata.DefinedMapMetaDataModel
 *  org.mule.common.metadata.ListMetaDataModel
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.ParameterizedMapMetaDataModel
 *  org.mule.common.metadata.PojoMetaDataModel
 *  org.mule.common.metadata.SimpleMetaDataModel
 *  org.mule.common.metadata.UnknownMetaDataModel
 *  org.mule.common.metadata.XmlMetaDataModel
 */
package org.mule.modules.openair.metadata.model.visitor;

import org.mule.common.metadata.DefinedMapMetaDataModel;
import org.mule.common.metadata.ListMetaDataModel;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.ParameterizedMapMetaDataModel;
import org.mule.common.metadata.PojoMetaDataModel;
import org.mule.common.metadata.SimpleMetaDataModel;
import org.mule.common.metadata.UnknownMetaDataModel;
import org.mule.common.metadata.XmlMetaDataModel;
import org.mule.modules.openair.metadata.model.OpenAirMetadataModel;
import org.mule.modules.openair.metadata.model.visitor.OpenAirMetadataModelVisitor;

public class DefaultOpenAirMetadataModelVisitor
implements OpenAirMetadataModelVisitor {
    public void visitPojoModel(PojoMetaDataModel pojoMetaDataModel) {
        this.defaultBehavior((MetaDataModel)pojoMetaDataModel);
    }

    public void visitListMetaDataModel(ListMetaDataModel listMetaDataModel) {
        this.defaultBehavior((MetaDataModel)listMetaDataModel);
    }

    public void visitSimpleMetaDataModel(SimpleMetaDataModel simpleMetaDataModel) {
        this.defaultBehavior((MetaDataModel)simpleMetaDataModel);
    }

    public void visitStaticMapModel(ParameterizedMapMetaDataModel parameterizedMapMetaDataModel) {
        this.defaultBehavior((MetaDataModel)parameterizedMapMetaDataModel);
    }

    public void visitDynamicMapModel(DefinedMapMetaDataModel definedMapMetaDataModel) {
        this.defaultBehavior((MetaDataModel)definedMapMetaDataModel);
    }

    public void visitXmlMetaDataModel(XmlMetaDataModel xmlMetaDataModel) {
        this.defaultBehavior((MetaDataModel)xmlMetaDataModel);
    }

    public void visitUnknownMetaDataModel(UnknownMetaDataModel unknownMetaDataModel) {
        this.defaultBehavior((MetaDataModel)unknownMetaDataModel);
    }

    @Override
    public void visitOpenAirMetaDataModelVisitor(OpenAirMetadataModel openAirMetaDataModel) {
        this.defaultBehavior((MetaDataModel)openAirMetaDataModel);
    }

    public void defaultBehavior(MetaDataModel metaDataModel) {
    }
}
