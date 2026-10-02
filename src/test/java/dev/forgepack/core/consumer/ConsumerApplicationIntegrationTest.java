package dev.forgepack.core.consumer;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.Repository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = ConsumerApplicationIntegrationTest.ConsumerApplication.class,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:consumer;DB_CLOSE_DELAY=-1",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
class ConsumerApplicationIntegrationTest {

    @org.springframework.beans.factory.annotation.Autowired
    private ConsumerRepository repository;

    @org.springframework.beans.factory.annotation.Autowired
    private ConsumerService service;

    @org.springframework.beans.factory.annotation.Autowired
    private ConsumerController controller;

    @org.springframework.beans.factory.annotation.Autowired
    private OpenAPI openAPI;

    @Test
    void consumerTypesAndLibraryConfigurationAreDiscoveredWithoutJpaScanAnnotations() {
        assertThat(repository).isInstanceOf(Repository.class);
        assertThat(service).isNotNull();
        assertThat(controller).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("API");
    }

    @SpringBootApplication
    static class ConsumerApplication {}
}