/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.metadata.MetaDataKey
 */
package org.mule.devkit.3.9.0.api.metadata;

import java.util.List;
import org.mule.common.metadata.MetaDataKey;

public interface ComposedMetaDataKey
extends MetaDataKey {
    public void addLevel(String var1, String var2);

    public Integer levels();

    public String getSeparator();

    public String getId(String var1);

    public String getDisplayName(String var1);

    public List<String> getSortedIds();

    public List<String> getSortedDisplayNames();
}
