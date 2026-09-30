package dev.forgepack.core.internal.configuration;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationHateoasTest {

    @Test
    void canBeInstantiated() {
        assertThat(new ConfigurationHateoas()).isNotNull();
    }
}
