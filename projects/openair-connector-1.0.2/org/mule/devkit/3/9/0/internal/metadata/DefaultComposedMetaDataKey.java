/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.MetaDataProperty
 *  org.mule.common.metadata.MetaDataPropertyManager
 *  org.mule.common.metadata.TypeMetaDataModel
 *  org.mule.common.metadata.key.property.MetaDataKeyProperty
 *  org.mule.common.metadata.key.property.dsql.DsqlFromMetaDataKeyProperty
 */
package org.mule.devkit.3.9.0.internal.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataProperty;
import org.mule.common.metadata.MetaDataPropertyManager;
import org.mule.common.metadata.TypeMetaDataModel;
import org.mule.common.metadata.key.property.MetaDataKeyProperty;
import org.mule.common.metadata.key.property.dsql.DsqlFromMetaDataKeyProperty;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKey;
import org.mule.devkit.3.9.0.api.metadata.exception.InvalidKeyException;
import org.mule.devkit.3.9.0.api.metadata.exception.InvalidSeparatorException;

public class DefaultComposedMetaDataKey
implements ComposedMetaDataKey,
TypeMetaDataModel {
    public static final String DEFAULT_KEY_SEPARATOR = "||";
    private static final String DEFAULT_CATEGORY = "DEFAULT";
    private Map<String, String> orderedKeyDisplayNamebyId = new LinkedHashMap<String, String>();
    private MetaDataPropertyManager<MetaDataKeyProperty> metaDataKeyPropertiesManager;
    private String category = "DEFAULT";
    private String customSeparator = "";

    public DefaultComposedMetaDataKey() {
        this.initializePropertiesManager();
    }

    public DefaultComposedMetaDataKey(String separator) {
        this();
        this.setSeparator(separator);
    }

    public DefaultComposedMetaDataKey(ComposedMetaDataKey origin) {
        this(origin.getSeparator());
        this.setCategory(origin.getCategory());
        Iterator<String> ids = origin.getSortedIds().iterator();
        Iterator<String> displayNames = origin.getSortedDisplayNames().iterator();
        while (ids.hasNext() && displayNames.hasNext()) {
            this.orderedKeyDisplayNamebyId.put(ids.next(), displayNames.next());
        }
        for (MetaDataKeyProperty property : origin.getProperties()) {
            this.addProperty(property);
        }
    }

    public DefaultComposedMetaDataKey(MetaDataKey defaultKey, String separator) {
        this(separator);
        if (defaultKey == null) {
            throw new InvalidKeyException("Source MetaDataKey cannot be null");
        }
        if (!StringUtils.contains((String)defaultKey.getId(), (String)separator)) {
            throw new InvalidSeparatorException(String.format("Separator not present in key. MetaDataKey id: %s label: %s", defaultKey.getId(), defaultKey.getDisplayName()));
        }
        this.buildLevelsFromKey(defaultKey, separator);
        for (MetaDataKeyProperty metaDataKeyProperty : defaultKey.getProperties()) {
            this.addProperty(metaDataKeyProperty);
        }
        this.setCategory(defaultKey.getCategory());
    }

    @Override
    public void addLevel(String levelId, String label) {
        if (StringUtils.isBlank((String)levelId) || StringUtils.isBlank((String)label) || this.orderedKeyDisplayNamebyId.containsKey(levelId)) {
            if (this.orderedKeyDisplayNamebyId.containsKey(levelId)) {
                throw new InvalidKeyException("Duplicated id " + levelId);
            }
            throw new InvalidKeyException("Key id and it's label cannot be null");
        }
        this.orderedKeyDisplayNamebyId.put(levelId, label);
    }

    public String getId() {
        return this.getId(this.customSeparator.isEmpty() ? DEFAULT_KEY_SEPARATOR : this.customSeparator);
    }

    @Override
    public Integer levels() {
        return this.orderedKeyDisplayNamebyId.size();
    }

    @Override
    public String getSeparator() {
        return this.customSeparator.isEmpty() ? DEFAULT_KEY_SEPARATOR : this.customSeparator;
    }

    private void setSeparator(String separator) {
        if (StringUtils.isBlank((String)separator)) {
            throw new InvalidSeparatorException("Separator cannot be empty nor blank");
        }
        this.customSeparator = separator;
    }

    public String getDisplayName() {
        return this.getDisplayName(this.customSeparator.isEmpty() ? DEFAULT_KEY_SEPARATOR : this.customSeparator);
    }

    @Override
    public String getId(String separator) {
        StringBuffer composedId = new StringBuffer();
        Iterator<Map.Entry<String, String>> it = this.orderedKeyDisplayNamebyId.entrySet().iterator();
        while (it.hasNext()) {
            composedId.append(it.next().getKey());
            if (!it.hasNext()) continue;
            composedId.append(separator);
        }
        return composedId.toString();
    }

    @Override
    public String getDisplayName(String separator) {
        StringBuffer composedLabel = new StringBuffer();
        Iterator<Map.Entry<String, String>> it = this.orderedKeyDisplayNamebyId.entrySet().iterator();
        while (it.hasNext()) {
            composedLabel.append(it.next().getValue());
            if (!it.hasNext()) continue;
            composedLabel.append(separator);
        }
        return composedLabel.toString();
    }

    @Override
    public List<String> getSortedIds() {
        return new ArrayList<String>(this.orderedKeyDisplayNamebyId.keySet());
    }

    @Override
    public List<String> getSortedDisplayNames() {
        return new ArrayList<String>(this.orderedKeyDisplayNamebyId.values());
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<MetaDataKeyProperty> getProperties() {
        return this.metaDataKeyPropertiesManager.getProperties();
    }

    public boolean addProperty(MetaDataKeyProperty metaDataKeyProperty) {
        return this.metaDataKeyPropertiesManager.addProperty((MetaDataProperty)metaDataKeyProperty);
    }

    public boolean removeProperty(MetaDataKeyProperty metaDataKeyProperty) {
        return this.metaDataKeyPropertiesManager.removeProperty((MetaDataProperty)metaDataKeyProperty);
    }

    public boolean hasProperty(Class<? extends MetaDataKeyProperty> metaDataKeyProperty) {
        return this.metaDataKeyPropertiesManager.hasProperty(metaDataKeyProperty);
    }

    public <T extends MetaDataKeyProperty> T getProperty(Class<T> metaDataKeyProperty) {
        return (T)((MetaDataKeyProperty)this.metaDataKeyPropertiesManager.getProperty(metaDataKeyProperty));
    }

    public String toString() {
        return "ComposedMetaDataKey:{ displayName:" + this.getDisplayName() + " id:" + this.getId() + " category:" + this.category + " }";
    }

    public int hashCode() {
        int result = this.getId() != null ? this.getId().hashCode() : 0;
        result = 31 * result + (this.category != null ? this.category.hashCode() : 0);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DefaultComposedMetaDataKey)) {
            return false;
        }
        DefaultComposedMetaDataKey that = (DefaultComposedMetaDataKey)obj;
        if (this.category != null ? !this.category.equals(that.category) : that.category != null) {
            return false;
        }
        return !(this.getId() != null ? !this.getId().equals(that.getId()) : that.getId() != null);
    }

    public int compareTo(MetaDataKey otherMetadataKey) {
        int res = this.category.compareTo(otherMetadataKey.getCategory());
        if (res != 0) {
            return res;
        }
        return this.getId().compareTo(otherMetadataKey.getId());
    }

    @Deprecated
    public boolean isFromCapable() {
        return this.metaDataKeyPropertiesManager.hasProperty(DsqlFromMetaDataKeyProperty.class);
    }

    private void buildLevelsFromKey(MetaDataKey defaultKey, String separator) {
        List<String> labels;
        List<String> ids = Arrays.asList(StringUtils.split((String)defaultKey.getId(), (String)separator));
        List<String> list = labels = StringUtils.isBlank((String)defaultKey.getDisplayName()) ? ids : Arrays.asList(StringUtils.split((String)defaultKey.getDisplayName(), (String)separator));
        if (labels.size() != ids.size()) {
            throw new InvalidKeyException("Invalid Key, a key must have a label for each keyId");
        }
        Iterator<String> idsIt = ids.iterator();
        Iterator<String> labelsIt = labels.iterator();
        while (idsIt.hasNext() && labelsIt.hasNext()) {
            this.addLevel(idsIt.next(), labelsIt.next());
        }
    }

    private void initializePropertiesManager() {
        this.metaDataKeyPropertiesManager = new MetaDataPropertyManager();
        this.metaDataKeyPropertiesManager.addProperty((MetaDataProperty)new DsqlFromMetaDataKeyProperty());
    }
}
