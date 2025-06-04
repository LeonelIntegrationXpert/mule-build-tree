/*
 * Decompiled with CFR 0.152.
 */
package org.mule.devkit.3.9.0.api.metadata;

import java.util.Map;
import java.util.Set;

public interface MetaDataKeyLevel {
    public Set<Map.Entry<String, String>> getIds();

    public MetaDataKeyLevel addId(String var1, String var2);
}
