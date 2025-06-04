/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool.KeyedPoolableObjectFactory
 *  org.apache.commons.pool.impl.GenericKeyedObjectPool
 *  org.apache.commons.pool.impl.GenericKeyedObjectPool$Config
 *  org.mule.config.PoolingProfile
 */
package org.mule.modules.openair.generated.pooling;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.mule.config.PoolingProfile;

public class DevkitGenericKeyedObjectPool
extends GenericKeyedObjectPool {
    public DevkitGenericKeyedObjectPool(KeyedPoolableObjectFactory factory, PoolingProfile connectionPoolingProfile) {
        super(factory, DevkitGenericKeyedObjectPool.toConfig(connectionPoolingProfile));
    }

    private static GenericKeyedObjectPool.Config toConfig(PoolingProfile connectionPoolingProfile) {
        GenericKeyedObjectPool.Config config = new GenericKeyedObjectPool.Config();
        if (connectionPoolingProfile != null) {
            config.maxIdle = connectionPoolingProfile.getMaxIdle();
            config.maxActive = connectionPoolingProfile.getMaxActive();
            config.maxWait = connectionPoolingProfile.getMaxWait();
            config.whenExhaustedAction = (byte)connectionPoolingProfile.getExhaustedAction();
            config.timeBetweenEvictionRunsMillis = connectionPoolingProfile.getEvictionCheckIntervalMillis();
            config.minEvictableIdleTimeMillis = connectionPoolingProfile.getMinEvictionMillis();
        }
        return config;
    }
}
