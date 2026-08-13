package dev.forgepack.core.internal.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configures the OpenAPI (Swagger) documentation bean for the application.
 *
 * <p>API metadata is configurable via {@code application.properties} under the
 * {@code forgepack.openapi} prefix. See {@link PropertiesOpenAPI}.</p>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
@Configuration
public class ConfigurationOpenAPI {

    private final PropertiesOpenAPI props;

    public ConfigurationOpenAPI(PropertiesOpenAPI props) {
        this.props = props;
    }

    /**
     * Builds and returns the {@link OpenAPI} definition with project metadata,
     * contact information, license, and server URL.
     *
     * @return the configured {@link OpenAPI} bean
     */
    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(props.title())
                        .version(props.version())
                        .contact(new Contact()
                                .name(props.contactName())
                                .url(props.contactUrl()))
                        .description(props.description())
                        .termsOfService(props.termsOfService())
                        .license(new License()
                                .name(props.licenseName())
                                .url(props.licenseUrl())))
                .servers(List.of(
                        new Server().url(props.serverUrl()).description("Server URL in environment")));
    }
}
