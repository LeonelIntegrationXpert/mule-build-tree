/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  org.apache.commons.io.IOUtils
 *  org.apache.log4j.Logger
 *  org.mule.common.Result$Status
 */
package org.mule.devkit.3.9.0.internal.metadata.cache;

import com.google.common.base.Optional;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.mule.common.Result;
import org.mule.devkit.3.9.0.internal.metadata.cache.PersistentMetaDataCache;

public class DefaultMetaDataCache
implements PersistentMetaDataCache {
    private static final Logger log = Logger.getLogger(DefaultMetaDataCache.class);
    private Map<Serializable, Serializable> cache = new HashMap<Serializable, Serializable>();
    private long lastModifiedFileTimeOnLoad = 0L;
    private boolean wasModified = true;
    private Path location = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Result.Status load(File persistentCache) throws IOException, ClassNotFoundException {
        log.debug((Object)("Loading cache from " + persistentCache.getPath()));
        Result.Status result = Result.Status.SUCCESS;
        if (persistentCache.length() > 0L) {
            FileInputStream cacheIS = new FileInputStream(persistentCache);
            try {
                FileLock fileLock = cacheIS.getChannel().lock(0L, persistentCache.length(), true);
                ObjectInputStream objIn = new ObjectInputStream(new BufferedInputStream(cacheIS));
                try {
                    this.cache = (Map)objIn.readObject();
                    this.wasModified = false;
                    log.debug((Object)"Loaded successfully");
                }
                catch (Exception e) {
                    log.error((Object)("An error occurred while loading the Cache " + persistentCache.getPath()));
                    result = Result.Status.FAILURE;
                }
                finally {
                    fileLock.release();
                    IOUtils.closeQuietly((InputStream)objIn);
                }
            }
            finally {
                IOUtils.closeQuietly((InputStream)cacheIS);
            }
        }
        this.location = persistentCache.toPath();
        this.lastModifiedFileTimeOnLoad = persistentCache.lastModified();
        return result;
    }

    @Override
    public Result.Status save(File persistentCache, boolean overwrite) {
        if (!this.wasModified) {
            return Result.Status.SUCCESS;
        }
        Result.Status result = Result.Status.FAILURE;
        try {
            if (!persistentCache.exists()) {
                log.error((Object)("Failed to write cache in path " + persistentCache.getPath() + ". File not found"));
            } else if (!this.persistedCacheWasModified(persistentCache) || overwrite) {
                this.writeCache(persistentCache);
                this.lastModifiedFileTimeOnLoad = persistentCache.lastModified();
                this.wasModified = false;
                result = Result.Status.SUCCESS;
                log.debug((Object)("Saved MetaDataCache to " + persistentCache.getPath()));
            } else {
                log.error((Object)"Cache was modified before this instance was persisted. To keep it consistent the cache will be destroyed");
            }
        }
        catch (Exception e) {
            log.error((Object)"An error occurred while persisting the cache");
        }
        return result;
    }

    @Override
    public Path getLocation() {
        return this.location;
    }

    @Override
    public void put(Serializable key, Serializable value) {
        this.cache.put(key, value);
        this.wasModified = true;
    }

    @Override
    public void putAll(Map<? extends Serializable, ? extends Serializable> values) {
        this.cache.putAll(values);
        this.wasModified = true;
    }

    @Override
    public <T> Optional<T> get(Serializable key) {
        return Optional.fromNullable((Object)this.cache.get(key));
    }

    private boolean persistedCacheWasModified(File persistentCache) {
        return persistentCache.lastModified() != this.lastModifiedFileTimeOnLoad;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeCache(File persistentCache) throws IOException, ClassNotFoundException {
        FileOutputStream cacheOS = null;
        ObjectOutputStream objOut = null;
        try {
            cacheOS = new FileOutputStream(persistentCache, false);
            FileLock fileLock = cacheOS.getChannel().lock();
            try {
                objOut = new ObjectOutputStream(new BufferedOutputStream(cacheOS));
                objOut.writeObject(this.cache);
                objOut.flush();
            }
            catch (Throwable throwable) {
                fileLock.release();
                IOUtils.closeQuietly(objOut);
                throw throwable;
            }
            fileLock.release();
            IOUtils.closeQuietly((OutputStream)objOut);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(cacheOS);
            throw throwable;
        }
        IOUtils.closeQuietly((OutputStream)cacheOS);
    }
}
