package dev.forgepack.core.internal.configuration;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing and exposes the {@link AuditorAware} bean used to
 * resolve the currently authenticated user as the entity auditor.
 *
 * <p>Activates {@code @CreatedBy} and {@code @LastModifiedBy} population on
 * audited entities via {@link ServiceAuditorAwareImpl}.</p>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 *
 * @see ServiceAuditorAwareImpl
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class ConfigurationAudit {

    /**
     * Provides the {@link AuditorAware} implementation that supplies the
     * current {@link String} for JPA auditing fields.
     *
     * @return a new {@link ServiceAuditorAwareImpl} instance
     */
    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }
}
