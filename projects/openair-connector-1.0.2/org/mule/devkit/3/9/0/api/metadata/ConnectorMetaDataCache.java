/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 */
package org.mule.devkit.3.9.0.api.metadata;

import com.google.common.base.Optional;
import java.io.Serializable;
import java.util.Map;

public interface ConnectorMetaDataCache {
    public void put(Serializable var1, Serializable var2);

    public void putAll(Map<? extends Serializable, ? extends Serializable> var1);

    public <T> Optional<T> get(Serializable var1);
}
