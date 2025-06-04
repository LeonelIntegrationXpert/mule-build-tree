/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.mule.common.metadata.DefaultMetaDataKey
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKeyBuilder$CombinationBuilder
 */
package org.mule.devkit.3.9.0.internal.metadata;

import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.MetaDataKey;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKey;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKeyBuilder;
import org.mule.devkit.3.9.0.api.metadata.exception.InvalidSeparatorException;
import org.mule.devkit.3.9.0.internal.metadata.DefaultComposedMetaDataKey;

public class InternalComposedMetaDataKeyBuilder
extends ComposedMetaDataKeyBuilder {
    private InternalComposedMetaDataKeyBuilder() {
    }

    public static ComposedMetaDataKey newKey() {
        return new DefaultComposedMetaDataKey();
    }

    public static ComposedMetaDataKey newKey(String separator) {
        return new DefaultComposedMetaDataKey(separator);
    }

    public static InternalComposedMetaDataKeyBuilder getInstance() {
        return new InternalComposedMetaDataKeyBuilder();
    }

    public static ComposedMetaDataKey fromSimpleKey(MetaDataKey defaultKey, String separator) {
        return new DefaultComposedMetaDataKey(defaultKey, separator);
    }

    public static MetaDataKey toSimpleKey(ComposedMetaDataKey composedKey, String keySeparator) {
        DefaultMetaDataKey metaDataKey = new DefaultMetaDataKey(composedKey.getId(keySeparator), composedKey.getDisplayName(keySeparator), composedKey.getProperties());
        metaDataKey.setCategory(composedKey.getCategory());
        return metaDataKey;
    }

    public static List<MetaDataKey> toSimpleKey(List<ComposedMetaDataKey> composedKeys, String keySeparator) {
        LinkedList<MetaDataKey> defaultMetaDataKeys = new LinkedList<MetaDataKey>();
        for (ComposedMetaDataKey metaDataKey : composedKeys) {
            defaultMetaDataKeys.add(InternalComposedMetaDataKeyBuilder.toSimpleKey(metaDataKey, keySeparator));
        }
        return defaultMetaDataKeys;
    }

    @Override
    public ComposedMetaDataKeyBuilder.CombinationBuilder newKeyCombination() {
        return super.newKeyCombination();
    }

    public InternalComposedMetaDataKeyBuilder withSeparator(String keySeparator) {
        if (!StringUtils.isBlank((String)keySeparator)) {
            this.customSeparator = keySeparator;
            return this;
        }
        throw new InvalidSeparatorException("A MetaDataKey separator cannot be empty");
    }
}
