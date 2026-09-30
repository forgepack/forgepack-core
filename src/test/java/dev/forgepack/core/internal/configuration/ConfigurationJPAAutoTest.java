package dev.forgepack.core.internal.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationJPAAutoTest {

    @Test
    void postProcessBeanDefinitionRegistry_registersEntityScanPackage() {
        DefaultListableBeanFactory registry = new DefaultListableBeanFactory();
        ConfigurationJPAAuto configuration = new ConfigurationJPAAuto();

        configuration.postProcessBeanDefinitionRegistry(registry);
        configuration.postProcessBeanFactory(registry);

        assertThat(registry.getBeanDefinitionCount()).isGreaterThan(0);
    }
}
