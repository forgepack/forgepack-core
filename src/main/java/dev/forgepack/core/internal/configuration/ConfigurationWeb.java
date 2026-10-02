package dev.forgepack.core.internal.configuration;

import dev.forgepack.core.internal.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for Forgepack's web error handling.
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
@AutoConfiguration
public class ConfigurationWeb {

    @Bean
    @ConditionalOnMissingBean(GlobalExceptionHandler.class)
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}