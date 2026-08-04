package dev.forgepack.core.internal.configuration;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScanPackages;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Auto-configuration that registers the library's component scan, JPA repositories,
 * and entity packages so they are discovered when the library is used as a dependency.
 *
 * <p>Scans both the {@code api} and {@code internal} packages for Spring components,
 * registers JPA repositories from the corresponding repository packages, and
 * registers the {@code internal.model} package for JPA entity scanning via
 * {@link EntityScanPackages}.</p>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
@AutoConfiguration
@ComponentScan(basePackages = {"dev.forgepack.core.api", "dev.forgepack.core.internal"})
@EnableJpaRepositories(basePackages = {"dev.forgepack.core.api.repository", "dev.forgepack.core.internal.repository"})
public class ConfigurationJPAAuto implements BeanDefinitionRegistryPostProcessor {

    /**
     * Registers the {@code dev.forgepack.core.internal.model} package for JPA
     * entity scanning, ensuring that library entities are included in the
     * application's persistence context.
     *
     * @param registry the bean definition registry
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        EntityScanPackages.register(registry, "dev.forgepack.core.internal.model");
    }

    /** No-op — bean factory post-processing is not required by this configuration. */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {}
}
