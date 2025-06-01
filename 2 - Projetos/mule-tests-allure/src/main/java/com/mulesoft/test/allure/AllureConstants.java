/*
 * Decompiled with CFR 0.152.
 */
package com.mulesoft.test.allure;

public interface AllureConstants {

    public static interface ServerPluginsFeature {
        public static final String SERVER_PLUGINS = "Server plugins";
    }

    public static interface FipsFeatures {
        public static final String JAVA_VERSION_11 = "Java 11";
        public static final String JAVA_VERSION_8 = "Java 8";

        public static interface FipsStories {
            public static final String MULE_RUNTIME_IN_FIPS_MODE = "Mule runtime in FIPS mode";
        }
    }

    public static interface Profiling {
        public static final String PROFILING_EE = "Profiling";

        public static interface ProfilingServiceStory {
            public static final String SERVER_PLUGIN = "Profiling Server Plugin";
        }
    }

    public static interface KryoSerializerFeature {
        public static final String KRYO = "Kryo serialization";

        public static interface KryoSerializerStory {
            public static final String JPMS_SERIALIZATION_COMPATIBILITY = "Serializers compatible with JPMS encapsulation";
            public static final String SERIALIZATION_COMPATIBILITY = "Serialization Compatibility with old reflection serializers";
        }
    }

    public static interface CacheFeature {
        public static final String CACHE = "Cache scope";
    }

    public static interface BatchFeature {
        public static final String BATCH = "Batch module";

        public static interface BatchStories {
            public static final String EL_FUNCTIONS = "Batch EL functions";
            public static final String JOB_MANAGEMENT = "Batch job management";
        }
    }

    public static interface ClusteringFeature {
        public static final String CLUSTERING = "Clustering";

        public static interface ClusteringStories {
            public static final String JDBC_PERSISTENCE = "ObjectStore JDBC persistence";
            public static final String DISTRIBUTED_MAP = "Distributed map";
            public static final String ERROR_HANDLING = "Error handling";
            public static final String QUORUM = "Quorum";
        }
    }

    public static interface FtpsFeature {
        public static final String FTPS_EXTENSION = "FTPS Extension";

        public static interface FtpsStory {
        }
    }

    public static interface MozartFeature {
        public static final String MOZART_USE_CASE = "Mozart Use Cases";
    }

    public static interface CompatibilityFeature {
        public static final String COMPATIBILITY = "Compatibility";

        public static interface Stories {
            public static final String IntegrationStory = "Integration between compatibility components and the core components";
            public static final String JMS_TRANSPORT = "JMS Transport";
            public static final String HTTP_TRANSPORT = "HTTP Transport";
            public static final String CXF_MODULE = "CXF Module";
            public static final String VM_TRANSPORT = "VM Transport";
            public static final String TCP_TRANSPORT = "TCP Transport";
            public static final String SSL_TRANSPORT = "SSL Transport";
            public static final String SPRING_EXTRAS = "Spring Extras";
        }
    }

    public static interface EeComponentsFeature {
        public static final String CORE_EE_COMPONENTS = "Core EE Components";

        public static interface DynamicEvaluateStory {
            public static final String DYNAMIC_EVALUATE = "Dynamic Evaluate";
        }

        public static interface TransformStory {
            public static final String TRANSFORM = "Transform";
        }
    }
}
