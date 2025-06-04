/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.common.Result$Status
 */
package org.mule.devkit.3.9.0.internal.metadata.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.mule.common.Result;
import org.mule.devkit.3.9.0.api.metadata.ConnectorMetaDataCache;

public interface PersistentMetaDataCache
extends ConnectorMetaDataCache {
    public Result.Status save(File var1, boolean var2);

    public Result.Status load(File var1) throws IOException, ClassNotFoundException;

    public Path getLocation();
}
