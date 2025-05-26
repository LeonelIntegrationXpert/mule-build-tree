package org.mule.runtime.globalconfig.api.cluster;

import org.mule.api.annotation.NoImplement;
import org.mule.runtime.globalconfig.api.EnableableConfig;

@NoImplement
public interface ClusterConfig {
    EnableableConfig getObjectStoreConfig();

    EnableableConfig getLockFactoryConfig();

    EnableableConfig getTimeSupplierConfig();

    EnableableConfig getQueueManager();

    EnableableConfig getClusterService();
}
