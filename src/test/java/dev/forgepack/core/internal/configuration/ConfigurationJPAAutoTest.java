package dev.forgepack.core.internal.configuration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationJPAAutoTest {

    @Test
    void canBeInstantiated() {
        assertThat(new ConfigurationJPAAuto()).isNotNull();
    }
}
