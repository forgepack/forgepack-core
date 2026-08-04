package dev.forgepack.core.internal.configuration;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the OpenAPI (Swagger) documentation.
 *
 * <p>Configurable via {@code application.properties} under the {@code forgepack.openapi}
 * prefix. All fields have sensible defaults and may be omitted by the consumer.</p>
 *
 * @param serverUrl      Server URL exposed in the Swagger UI. Default: {@code http://localhost:8080}.
 * @param title          API title. Default: {@code API}.
 * @param version        API version. Default: {@code 1.0.0}.
 * @param description    API description. Default: blank.
 * @param termsOfService URL to the terms of service. Default: blank.
 * @param contactName    Contact name. Default: blank.
 * @param contactUrl     Contact URL. Default: blank.
 * @param licenseName    License name. Default: blank.
 * @param licenseUrl     License URL. Default: blank.
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
@Validated
@ConfigurationProperties(prefix = "forgepack.openapi")
public record PropertiesOpenAPI(
        @NotBlank @DefaultValue("http://localhost:8080") String serverUrl,
        @NotBlank @DefaultValue("API")                   String title,
        @NotBlank @DefaultValue("1.0.0")                 String version,
                  @DefaultValue("")                      String description,
                  @DefaultValue("")                      String termsOfService,
                  @DefaultValue("")                      String contactName,
                  @DefaultValue("")                      String contactUrl,
                  @DefaultValue("")                      String licenseName,
                  @DefaultValue("")                      String licenseUrl
) {}
