/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.util.StringUtils
 */
package org.mule.devkit.3.9.0.api.metadata;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.mule.devkit.3.9.0.api.metadata.MetaDataKeyLevel;
import org.mule.devkit.3.9.0.api.metadata.exception.InvalidKeyException;
import org.mule.util.StringUtils;

public class DefaultMetaDataKeyLevel
implements MetaDataKeyLevel {
    private Map<String, String> idsForLevel = new LinkedHashMap<String, String>();

    @Override
    public Set<Map.Entry<String, String>> getIds() {
        return this.idsForLevel.entrySet();
    }

    @Override
    public MetaDataKeyLevel addId(String id, String label) {
        if (StringUtils.isBlank((String)id) || StringUtils.isBlank((String)label)) {
            throw new InvalidKeyException("Neither id nor label can be blank");
        }
        if (this.idsForLevel.containsKey(id)) {
            throw new InvalidKeyException("Duplicated Id in level");
        }
        this.idsForLevel.put(id, label);
        return this;
    }
}
