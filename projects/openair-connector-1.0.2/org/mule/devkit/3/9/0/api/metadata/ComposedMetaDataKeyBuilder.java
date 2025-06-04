/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKeyBuilder$CombinationBuilder
 */
package org.mule.devkit.3.9.0.api.metadata;

import java.util.LinkedList;
import java.util.List;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKey;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKeyBuilder;

public class ComposedMetaDataKeyBuilder {
    protected List<ComposedMetaDataKey> keys = new LinkedList<ComposedMetaDataKey>();
    protected String customSeparator = "";

    protected ComposedMetaDataKeyBuilder() {
    }

    public static ComposedMetaDataKeyBuilder getInstance() {
        return new ComposedMetaDataKeyBuilder();
    }

    public CombinationBuilder newKeyCombination() {
        return new CombinationBuilder(this, this.customSeparator);
    }

    public ComposedMetaDataKeyBuilder endKeyCombination(List<ComposedMetaDataKey> keys) {
        this.keys.addAll(keys);
        return this;
    }

    public List<ComposedMetaDataKey> build() {
        List<ComposedMetaDataKey> result = this.keys;
        this.resetBuild();
        return result;
    }

    private void resetBuild() {
        this.keys = new LinkedList<ComposedMetaDataKey>();
    }
}
