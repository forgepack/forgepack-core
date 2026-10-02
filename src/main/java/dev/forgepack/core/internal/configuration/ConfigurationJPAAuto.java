package dev.forgepack.core.internal.configuration;

import dev.forgepack.core.internal.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration that registers the library's components when it is used as a
 * dependency.
 *
 * <p>Consumer entities and repositories remain under Spring Boot's conventional
 * discovery, rooted at the consumer application's {@code @SpringBootApplication}
 * package.</p>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
@AutoConfiguration
@ComponentScan(
    basePackages = {"dev.forgepack.core.api", "dev.forgepack.core.internal"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = GlobalExceptionHandler.class))
@Import(GlobalExceptionHandler.class)
@EnableConfigurationProperties({PropertiesOpenAPI.class})
public class ConfigurationJPAAuto {}
