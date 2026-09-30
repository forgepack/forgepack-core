package dev.forgepack.core.internal.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.data.auditing.DateTimeProvider;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationAuditTest {

    @Test
    void dateTimeProvider_returnsCurrentTimestamp() {
        ConfigurationAudit configuration = new ConfigurationAudit();

        DateTimeProvider provider = configuration.dateTimeProvider();

        assertThat(provider.getNow()).isPresent();
    }
}
