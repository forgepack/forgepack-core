package dev.forgepack.core.internal.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

/**
 * Configures Spring Data Web support with DTO-based {@code Page} serialization.
 *
 * <p>Activating {@link EnableSpringDataWebSupport.PageSerializationMode#VIA_DTO VIA_DTO}
 * ensures that paginated responses are serialized through a stable DTO structure
 * instead of the internal {@code PageImpl} representation, preventing breaking
 * changes across Spring Data upgrades.</p>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
class ConfigurationHateoas { }
