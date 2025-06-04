package org.mule.runtime.globalconfig.internal;

import com.typesafe.config.Config;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Map.Entry;
import org.mule.maven.client.api.model.Authentication;
import org.mule.maven.client.api.model.MavenConfiguration;
import org.mule.maven.client.api.model.RemoteRepository;
import org.mule.maven.client.api.model.RepositoryPolicy;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.api.i18n.I18nMessageFactory;
import org.mule.runtime.container.api.MuleFoldersUtil;
import org.mule.runtime.globalconfig.api.exception.RuntimeGlobalConfigException;

@SuppressWarnings("unchecked")  // ← para casts do unwrapped()
public class MavenConfigBuilder {

    private static final String POSITION = "position";

    /** Constrói a configuração Maven a partir do bloco <mavenConfig> do runtime-global-config. */
    public static MavenConfiguration buildMavenConfig(Config mavenConfig) {
        try {
            /* ──────────────── Lê paths e flags primitivas ──────────────── */
            String globalSettingsLocation    = mavenConfig.hasPath("globalSettingsLocation")    ? mavenConfig.getString("globalSettingsLocation")    : null;
            String userSettingsLocation      = mavenConfig.hasPath("userSettingsLocation")      ? mavenConfig.getString("userSettingsLocation")      : null;
            String settingsSecurityLocation  = mavenConfig.hasPath("settingsSecurityLocation")  ? mavenConfig.getString("settingsSecurityLocation")  : null;
            String repositoryLocation        = mavenConfig.hasPath("repositoryLocation")        ? mavenConfig.getString("repositoryLocation")        : null;
            boolean ignoreArtifactDescriptor =
                    !mavenConfig.hasPath("ignoreArtifactDescriptorRepositories")
                            || mavenConfig.getBoolean("ignoreArtifactDescriptorRepositories");
            boolean forceUpdateNever  = mavenConfig.hasPath("forcePolicyUpdateNever")  && mavenConfig.getBoolean("forcePolicyUpdateNever");
            boolean forceUpdateAlways = !forceUpdateNever &&
                    mavenConfig.hasPath("forcePolicyUpdateAlways") && mavenConfig.getBoolean("forcePolicyUpdateAlways");

            List<String> activeProfiles   = mavenConfig.hasPath("activeProfiles")
                    ? mavenConfig.getStringList("activeProfiles")
                    : Collections.emptyList();
            List<String> inactiveProfiles = mavenConfig.hasPath("inactiveProfiles")
                    ? mavenConfig.getStringList("inactiveProfiles")
                    : Collections.emptyList();
            boolean offLineMode = mavenConfig.hasPath("offLineMode") && mavenConfig.getBoolean("offLineMode");

            /* ──────────────── Resolve diretórios locais ──────────────── */
            File globalSettingsFile   = findResource(globalSettingsLocation);
            File userSettingsFile     = findResource(userSettingsLocation);
            File settingsSecurityFile = findResource(settingsSecurityLocation);

            File localRepo = getRuntimeRepositoryFolder();
            if (repositoryLocation != null) {
                localRepo = new File(repositoryLocation);
                if (!localRepo.exists()) {
                    throw new RuntimeGlobalConfigException(
                            I18nMessageFactory.createStaticMessage(
                                    String.format("Repository folder %s configured for the Mule runtime does not exist", repositoryLocation)));
                }
            }

            /* ──────────────── Builder inicial básico ──────────────── */
            MavenConfiguration.MavenConfigurationBuilder cfgBuilder = MavenConfiguration
                    .newMavenConfigurationBuilder()
                    .localMavenRepositoryLocation(localRepo)
                    .ignoreArtifactDescriptorRepositories(ignoreArtifactDescriptor)
                    .forcePolicyUpdateNever(forceUpdateNever)
                    .forcePolicyUpdateAlways(forceUpdateAlways)
                    .activeProfiles(activeProfiles)
                    .inactiveProfiles(inactiveProfiles)
                    .offlineMode(offLineMode);

            if (globalSettingsFile   != null) cfgBuilder.globalSettingsLocation(globalSettingsFile);
            if (userSettingsFile     != null) cfgBuilder.userSettingsLocation(userSettingsFile);
            if (settingsSecurityFile != null) cfgBuilder.settingsSecurityLocation(settingsSecurityFile);

            /* ──────────────── Repositórios remotos ──────────────── */
            if (mavenConfig.hasPath("repositories")) {
                Map<String, Object> rawRepos = mavenConfig.getObject("repositories").unwrapped();
                Map<String, Map<String, Object>> repos = rawRepos.entrySet().stream()
                        .filter(e -> e.getValue() instanceof Map)
                        .collect(
                                java.util.stream.Collectors.toMap(
                                        Map.Entry::getKey,
                                        e -> (Map<String, Object>) e.getValue()
                                )
                        );

                repos.entrySet().stream()
                        .sorted(remoteRepositoriesComparator())
                        .forEach(repoEntry -> addRemoteRepository(cfgBuilder, repoEntry));
            }

            /* ──────────────── Propriedades do usuário ──────────────── */
            if (mavenConfig.hasPath("userProperties")) {
                Properties userProps = extractUserProperties(mavenConfig);
                cfgBuilder.userProperties(userProps);
            }

            return cfgBuilder.build();
        } catch (RuntimeGlobalConfigException e) {
            throw e;             // já é exceção esperada
        } catch (Exception e) {  // converte qualquer outro erro
            throw new RuntimeGlobalConfigException(e);
        }
    }

    /* ------------------------------------------------------------------ */
    /* Helpers                                                            */
    /* ------------------------------------------------------------------ */

    private static void addRemoteRepository(MavenConfiguration.MavenConfigurationBuilder cfgBuilder,
                                            Entry<String, Map<String, Object>> repoEntry) {
        String repoId = repoEntry.getKey();
        Map<String, Object> repoCfg = repoEntry.getValue();

        String url      = String.valueOf(repoCfg.get("url"));
        String username = (String) repoCfg.get("username");
        String password = (String) repoCfg.get("password");

        try {
            RemoteRepository.RemoteRepositoryBuilder repoBuilder = RemoteRepository
                    .newRemoteRepositoryBuilder()
                    .id(repoId)
                    .url(new URL(url));

            /* autenticação opcional */
            if (username != null || password != null) {
                Authentication.AuthenticationBuilder auth = Authentication.newAuthenticationBuilder();
                if (username != null) auth.username(username);
                if (password != null) auth.password(password);
                repoBuilder.authentication(auth.build());
            }

            /* snapshot / release policies */
            getRepositoryPolicy(repoCfg, "snapshotPolicy")
                    .ifPresent(repoBuilder::snapshotPolicy);
            getRepositoryPolicy(repoCfg, "releasePolicy")
                    .ifPresent(repoBuilder::releasePolicy);

            cfgBuilder.remoteRepository(repoBuilder.build());

        } catch (MalformedURLException ex) {
            throw new MuleRuntimeException(ex);
        }
    }

    private static Optional<RepositoryPolicy> getRepositoryPolicy(Map<String, Object> repoCfg, String key) {
        if (!repoCfg.containsKey(key)) {
            return Optional.empty();
        }
        Map<String, Object> policyMap = (Map<String, Object>) repoCfg.get(key);

        RepositoryPolicy.RepositoryPolicyBuilder pb = RepositoryPolicy.newRepositoryPolicyBuilder();

        Object enabled        = policyMap.get("enabled");
        Object updatePolicy   = policyMap.get("updatePolicy");
        Object checksumPolicy = policyMap.get("checksumPolicy");

        if (enabled != null)        pb.enabled(Boolean.parseBoolean(String.valueOf(enabled)));
        if (updatePolicy != null)   pb.updatePolicy(String.valueOf(updatePolicy));
        if (checksumPolicy != null) pb.checksumPolicy(String.valueOf(checksumPolicy));

        return Optional.of(pb.build());
    }

    private static File findResource(String resourceLocation) {
        if (resourceLocation == null) return null;

        URL   cpResource   = MavenConfigBuilder.class.getResource(resourceLocation);
        File  absoluteFile = new File(resourceLocation);

        if (cpResource == null && !absoluteFile.exists()) {
            throw new RuntimeGlobalConfigException(
                    I18nMessageFactory.createStaticMessage(
                            String.format("Couldn't find file %s either on the classpath or as absolute path", resourceLocation)));
        }
        return cpResource != null ? new File(cpResource.getFile()) : absoluteFile;
    }

    /** Ordena repositórios pela chave “position”; ausente ⇒ Integer.MAX_VALUE. */
    private static Comparator<Entry<String, Map<String, Object>>> remoteRepositoriesComparator() {
        return (e1, e2) -> {
            int p1 = Integer.parseInt(String.valueOf(e1.getValue().getOrDefault(POSITION, Integer.MAX_VALUE)));
            int p2 = Integer.parseInt(String.valueOf(e2.getValue().getOrDefault(POSITION, Integer.MAX_VALUE)));
            return Integer.compare(p1, p2);
        };
    }

    private static File getRuntimeRepositoryFolder() {
        return new File(MuleFoldersUtil.getMuleBaseFolder(), "repository");
    }

    private static Properties extractUserProperties(Config mavenConfig) {
        Properties userProps = new Properties();
        Map<String, Object> rawProps = mavenConfig.getObject("userProperties").unwrapped();

        for (Map.Entry<String, Object> entry : rawProps.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> nestedMap = (Map<String, Object>) entry.getValue();
                Object value = nestedMap.get("value");
                if (value != null) {
                    userProps.put(entry.getKey(), String.valueOf(value));
                }
            }
        }

        return userProps;
    }

    /** Configuração mínima, apontando apenas para o repositório local do runtime. */
    public static MavenConfiguration defaultMavenConfig() {
        return MavenConfiguration
                .newMavenConfigurationBuilder()
                .localMavenRepositoryLocation(getRuntimeRepositoryFolder())
                .build();
    }
}
