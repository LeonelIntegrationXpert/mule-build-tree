/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.MetaDataModelVisitor
 */
package org.mule.modules.openair.metadata.model.visitor;

import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.MetaDataModelVisitor;

public interface SearchMetadataVisitor
extends MetaDataModelVisitor {
    public MetaDataModel getModel();
}
