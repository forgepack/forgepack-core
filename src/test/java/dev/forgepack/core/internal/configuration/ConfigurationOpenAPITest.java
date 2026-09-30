package dev.forgepack.core.internal.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationOpenAPITest {

    @Test
    void openAPI_buildsDefinitionFromProperties() {
        PropertiesOpenAPI props = new PropertiesOpenAPI(
                "http://api.example.com", "Forgepack API", "2.0.0",
                "Description", "http://terms", "Support", "http://support",
                "MIT", "http://license");
        ConfigurationOpenAPI configuration = new ConfigurationOpenAPI(props);

        OpenAPI openAPI = configuration.openAPI();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Forgepack API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("2.0.0");
        assertThat(openAPI.getInfo().getDescription()).isEqualTo("Description");
        assertThat(openAPI.getInfo().getTermsOfService()).isEqualTo("http://terms");
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("Support");
        assertThat(openAPI.getInfo().getContact().getUrl()).isEqualTo("http://support");
        assertThat(openAPI.getInfo().getLicense().getName()).isEqualTo("MIT");
        assertThat(openAPI.getInfo().getLicense().getUrl()).isEqualTo("http://license");
        assertThat(openAPI.getServers()).hasSize(1);
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo("http://api.example.com");
    }

    @Test
    void propertiesOpenAPI_accessorsExposeConstructorValues() {
        PropertiesOpenAPI props = new PropertiesOpenAPI(
                "http://localhost:8080", "API", "1.0.0", "", "", "", "", "", "");

        assertThat(props.serverUrl()).isEqualTo("http://localhost:8080");
        assertThat(props.title()).isEqualTo("API");
        assertThat(props.version()).isEqualTo("1.0.0");
        assertThat(props.description()).isEmpty();
        assertThat(props.termsOfService()).isEmpty();
        assertThat(props.contactName()).isEmpty();
        assertThat(props.contactUrl()).isEmpty();
        assertThat(props.licenseName()).isEmpty();
        assertThat(props.licenseUrl()).isEmpty();
    }
}
