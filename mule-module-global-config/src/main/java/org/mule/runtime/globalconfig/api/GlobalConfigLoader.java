package org.mule.runtime.globalconfig.api;

import com.typesafe.config.*;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.mule.maven.client.api.model.MavenConfiguration;
import org.mule.runtime.api.i18n.I18nMessageFactory;
import org.mule.runtime.api.util.LazyValue;
import org.mule.runtime.globalconfig.api.cluster.ClusterConfig;
import org.mule.runtime.globalconfig.api.exception.RuntimeGlobalConfigException;
import org.mule.runtime.globalconfig.internal.ClusterConfigBuilder;
import org.mule.runtime.globalconfig.internal.MavenConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Function;
import java.util.function.Supplier;

public class GlobalConfigLoader {
    private static final String CONFIG_ROOT_ELEMENT_NAME = "muleRuntimeConfig";
    private static final String DEFAULT_MULE_CONFIG_FILE_NAME = "mule-config";
    public static final String MULE_CONFIG_FILE_NAME_PROPERTY = "mule.configFile";
    private static final String CLUSTER_PROPERTY = "cluster";
    private static final String MAVEN_PROPERTY = "maven";
    private static final String JSON_EXTENSION = ".json";
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalConfigLoader.class);
    private static MavenConfiguration mavenConfig;
    private static ClusterConfig clusterConfig;
    private static final StampedLock lock = new StampedLock();
    private static final String MULE_SCHEMA_JSON_LOCATION = "mule-schema.json";

    private GlobalConfigLoader() {
    }

    private static void initialiseGlobalConfig() {
        String configFileName = System.getProperty(MULE_CONFIG_FILE_NAME_PROPERTY, DEFAULT_MULE_CONFIG_FILE_NAME)
                .replace(JSON_EXTENSION, "");

        Config config = ConfigFactory.load(
                GlobalConfigLoader.class.getClassLoader(),
                configFileName,
                ConfigParseOptions.defaults().setSyntax(ConfigSyntax.JSON),
                ConfigResolveOptions.defaults()
        );

        Config muleRuntimeConfig = config.hasPath(CONFIG_ROOT_ELEMENT_NAME) ? config.getConfig(CONFIG_ROOT_ELEMENT_NAME) : null;

        if (muleRuntimeConfig == null) {
            mavenConfig = MavenConfigBuilder.defaultMavenConfig();
            clusterConfig = ClusterConfigBuilder.defaultClusterConfig();
        } else {
            String effectiveConfigAsJson = muleRuntimeConfig.root()
                    .render(ConfigRenderOptions.concise().setJson(true).setComments(false));

            LazyValue<String> prettyPrintConfig = new LazyValue<>(
                    () -> muleRuntimeConfig.root().render(
                            ConfigRenderOptions.defaults().setComments(true).setJson(true).setFormatted(true)));

            try (InputStream schemaStream = GlobalConfigLoader.class.getClassLoader()
                    .getResourceAsStream(MULE_SCHEMA_JSON_LOCATION)) {

                if (schemaStream == null) {
                    throw new RuntimeGlobalConfigException(
                            I18nMessageFactory.createStaticMessage(
                                    String.format("Resource %s not found in classpath.", MULE_SCHEMA_JSON_LOCATION))
                    );
                }

                JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
                Schema schema = SchemaLoader.load(rawSchema);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using effective mule-config.json configuration:\n{}", prettyPrintConfig.get());
                }

                schema.validate(new JSONObject(effectiveConfigAsJson));

                parseMavenConfig(muleRuntimeConfig);
                parseClusterConfig(muleRuntimeConfig);

            } catch (ValidationException e) {
                LOGGER.info("Mule global config exception. Effective configuration is (config is a merge of MULE_HOME/conf/{}.json and system properties):\n{}",
                        configFileName, prettyPrintConfig.get());
                throw new RuntimeGlobalConfigException(e);
            } catch (IOException e) {
                throw new RuntimeGlobalConfigException(
                        I18nMessageFactory.createStaticMessage(String.format("resources %s missing from the runtime classpath", MULE_SCHEMA_JSON_LOCATION)),
                        e);
            }
        }
    }

    private static <T> T parseConfig(Config muleRuntimeConfig, String configProperty,
                                     Supplier<T> noConfigCallback, Function<Config, T> parseConfigCallback) {
        Config config = muleRuntimeConfig.hasPath(configProperty)
                ? muleRuntimeConfig.getConfig(configProperty)
                : null;
        return config == null ? noConfigCallback.get() : parseConfigCallback.apply(config);
    }

    private static void parseClusterConfig(Config muleRuntimeConfig) {
        clusterConfig = parseConfig(muleRuntimeConfig, CLUSTER_PROPERTY,
                ClusterConfigBuilder::defaultClusterConfig,
                ClusterConfigBuilder::parseClusterConfig);
    }

    private static void parseMavenConfig(Config muleRuntimeConfig) {
        mavenConfig = parseConfig(muleRuntimeConfig, MAVEN_PROPERTY,
                MavenConfigBuilder::defaultMavenConfig,
                MavenConfigBuilder::buildMavenConfig);
    }

    public static void reset() {
        long stamp = lock.writeLock();
        try {
            mavenConfig = null;
            clusterConfig = null;
            ConfigFactory.invalidateCaches();
            initialiseGlobalConfig();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public static MavenConfiguration getMavenConfig() {
        return safelyGetConfig(() -> mavenConfig);
    }

    public static void setMavenConfig(MavenConfiguration mavenConfig) {
        GlobalConfigLoader.mavenConfig = mavenConfig;
    }

    public static ClusterConfig getClusterConfig() {
        return safelyGetConfig(() -> clusterConfig);
    }

    private static <T> T safelyGetConfig(Supplier<T> configSupplier) {
        long stamp = lock.readLock();
        try {
            if (configSupplier.get() == null) {
                long writeStamp = lock.tryConvertToWriteLock(stamp);
                if (writeStamp == 0L) {
                    lock.unlockRead(stamp);
                    stamp = lock.writeLock();
                } else {
                    stamp = writeStamp;
                }
                if (configSupplier.get() == null) {
                    initialiseGlobalConfig();
                }
            }
            return configSupplier.get();
        } finally {
            lock.unlock(stamp);
        }
    }
}
