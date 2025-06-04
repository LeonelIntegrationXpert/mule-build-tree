/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.typesafe.config.Config
 */
package org.mule.runtime.globalconfig.internal;

import com.typesafe.config.Config;
import org.mule.runtime.globalconfig.api.EnableableConfig;
import org.mule.runtime.globalconfig.api.cluster.ClusterConfig;
import org.mule.runtime.globalconfig.api.exception.RuntimeGlobalConfigException;
import org.mule.runtime.globalconfig.internal.DefaultEnableableConfig;

public class ClusterConfigBuilder {
    public static ClusterConfig defaultClusterConfig() {
        ClusterConfigImpl clusterConfig = new ClusterConfigImpl();
        clusterConfig.lockFactoryConfig = new DefaultEnableableConfig(true);
        clusterConfig.objectStoreConfig = new DefaultEnableableConfig(true);
        clusterConfig.timeSupplierConfig = new DefaultEnableableConfig(true);
        clusterConfig.queueManagerConfig = new DefaultEnableableConfig(true);
        clusterConfig.clusterServiceConfig = new DefaultEnableableConfig(true);
        return clusterConfig;
    }

    public static ClusterConfig parseClusterConfig(Config mavenConfig) {
        ClusterConfigImpl clusterConfig = new ClusterConfigImpl();
        try {
            clusterConfig.objectStoreConfig = ClusterConfigBuilder.parseEnabledConfig(mavenConfig, "objectStore");
            clusterConfig.lockFactoryConfig = ClusterConfigBuilder.parseEnabledConfig(mavenConfig, "lockFactory");
            clusterConfig.timeSupplierConfig = ClusterConfigBuilder.parseEnabledConfig(mavenConfig, "timeSupplier");
            clusterConfig.queueManagerConfig = ClusterConfigBuilder.parseEnabledConfig(mavenConfig, "queueManager");
            clusterConfig.clusterServiceConfig = ClusterConfigBuilder.parseEnabledConfig(mavenConfig, "clusterService");
            return clusterConfig;
        }
        catch (Exception e) {
            if (e instanceof RuntimeGlobalConfigException) {
                throw e;
            }
            throw new RuntimeGlobalConfigException(e);
        }
    }

    private static DefaultEnableableConfig parseEnabledConfig(Config clusterConfig, String propertyName) {
        Config enabledConfig;
        Config config = enabledConfig = clusterConfig.hasPath(propertyName) ? clusterConfig.getConfig(propertyName) : null;
        return enabledConfig == null ? new DefaultEnableableConfig(true) : new DefaultEnableableConfig(enabledConfig.hasPath("enabled") ? enabledConfig.getBoolean("enabled") : true);
    }

    public static class ClusterConfigImpl
    implements ClusterConfig {
        private EnableableConfig objectStoreConfig;
        private EnableableConfig lockFactoryConfig;
        private EnableableConfig timeSupplierConfig;
        private EnableableConfig queueManagerConfig;
        private EnableableConfig clusterServiceConfig;

        @Override
        public EnableableConfig getObjectStoreConfig() {
            return this.objectStoreConfig;
        }

        @Override
        public EnableableConfig getLockFactoryConfig() {
            return this.lockFactoryConfig;
        }

        @Override
        public EnableableConfig getTimeSupplierConfig() {
            return this.timeSupplierConfig;
        }

        @Override
        public EnableableConfig getQueueManager() {
            return this.queueManagerConfig;
        }

        @Override
        public EnableableConfig getClusterService() {
            return this.clusterServiceConfig;
        }
    }
}
