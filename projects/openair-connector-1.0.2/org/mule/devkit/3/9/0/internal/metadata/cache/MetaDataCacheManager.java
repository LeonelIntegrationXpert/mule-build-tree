/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FileUtils
 *  org.apache.log4j.Logger
 *  org.mule.common.Result$Status
 */
package org.mule.devkit.3.9.0.internal.metadata.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.mule.common.Result;
import org.mule.devkit.3.9.0.api.metadata.ConnectorMetaDataCache;
import org.mule.devkit.3.9.0.internal.metadata.cache.DefaultMetaDataCache;
import org.mule.devkit.3.9.0.internal.metadata.cache.PersistentMetaDataCache;

public class MetaDataCacheManager {
    private static final String ROOT_PATH = Paths.get(System.getProperty("java.io.tmpdir"), "connector_internal_cache").toString();
    private static final Logger log = Logger.getLogger(MetaDataCacheManager.class);

    public static ConnectorMetaDataCache getCache(String projectName, String namespace, String id, String version, String hashedConfigurables) {
        DefaultMetaDataCache cache = new DefaultMetaDataCache();
        try {
            File persistentCache = new File(Paths.get(ROOT_PATH, projectName, namespace, id, version, hashedConfigurables).toUri());
            if (!persistentCache.exists()) {
                log.debug((Object)("Initializing Cache at " + persistentCache.getPath()));
                MetaDataCacheManager.initializeCacheFile(persistentCache);
            }
            if (cache.load(persistentCache) != Result.Status.SUCCESS) {
                log.error((Object)("An error occurred while loading the cache " + persistentCache.getPath()));
            }
        }
        catch (Exception e) {
            log.error((Object)("An error occurred while initializing the cache " + e.getMessage()));
        }
        return cache;
    }

    public static void destroyCache(String projectName, String namespace, String id, String version, String hashedConfigurables) {
        MetaDataCacheManager.destroyCache(new File(Paths.get(ROOT_PATH, projectName, namespace, id, version, hashedConfigurables).toUri()));
    }

    public static void save(ConnectorMetaDataCache cache) {
        if (cache != null && ((PersistentMetaDataCache)cache).getLocation() != null) {
            try {
                File persistedCache = new File(((PersistentMetaDataCache)cache).getLocation().toUri());
                boolean forceWrite = false;
                if (!persistedCache.exists()) {
                    MetaDataCacheManager.initializeCacheFile(persistedCache);
                    forceWrite = true;
                }
                log.debug((Object)("Attempting to save Cache to " + persistedCache.getPath()));
                log.debug((Object)("Overwrite cache " + forceWrite));
                if (((DefaultMetaDataCache)cache).save(persistedCache, forceWrite) != Result.Status.SUCCESS) {
                    MetaDataCacheManager.destroyCache(persistedCache);
                }
            }
            catch (Exception e) {
                log.error((Object)("Failed to save cache." + e.getMessage()));
            }
        }
    }

    private static void initializeCacheFile(File persistentCache) throws IOException {
        persistentCache.getParentFile().mkdirs();
        if (!persistentCache.createNewFile()) {
            log.error((Object)("Failed to initialize cache in path " + persistentCache.getPath()));
        }
    }

    private static void destroyCache(File invalidCache) {
        if (invalidCache.exists()) {
            try {
                FileUtils.forceDelete((File)invalidCache);
            }
            catch (Exception e) {
                log.error((Object)("Failed to destroy cache " + invalidCache.getPath() + ". " + e.getMessage()));
            }
        }
    }
}
