package org.mule.runtime.globalconfig.api.maven;

import java.util.function.Supplier;

import org.mule.maven.client.api.MavenClient;
import org.mule.maven.client.api.MavenClientProvider;
import org.mule.maven.client.api.model.MavenConfiguration;
import org.mule.runtime.api.util.LazyValue;

public class MavenClientFactory {

    // Tipagem correta com generics, sem raw types
    public static Supplier<MavenClientProvider> mavenClientProvider =
            new LazyValue<>(() -> MavenClientProvider.discoverProvider(MavenClientProvider.class.getClassLoader()));

    public static MavenClient createMavenClient(MavenConfiguration mavenConfiguration) {
        return mavenClientProvider.get().createMavenClient(mavenConfiguration);
    }

    public static void setMavenClientProvider(Supplier<MavenClientProvider> customProvider) {
        mavenClientProvider = customProvider;
    }
}
