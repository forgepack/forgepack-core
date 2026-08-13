package dev.forgepack.core.internal.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.repository.RepositoryCrud;
import dev.forgepack.core.internal.model.GenericAuditEntity;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceUtilsTest {

    @Mock RepositoryCrud<TestEntity> repository;
    @Mock Mapper<TestEntity, TestRequest, TestResponse> mapper;

    ServiceUtils<TestEntity, TestRequest, TestResponse> service;

    @BeforeEach
    void setUp() {
        service = new ServiceUtils<>(TestEntity.class, repository, mapper);
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setScheme("http");
        httpRequest.setServerName("localhost");
        httpRequest.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    // --- existsEntity ---

    @Test
    void existsEntity_found_returnsEntity() {
        UUID id = UUID.randomUUID();
        TestEntity entity = new TestEntity();
        given(repository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.of(entity));

        assertThat(service.existsEntity("update", id)).isSameAs(entity);
    }

    @Test
    void existsEntity_notFound_throwsWithActionEntityAndId() {
        UUID id = UUID.randomUUID();
        given(repository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.existsEntity("delete", id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("delete")
            .hasMessageContaining("TestEntity")
            .hasMessageContaining(id.toString());
    }

    @Test
    void existsEntity_softDeleted_throwsEntityNotFoundException() {
        // findByIdAndDeletedAtIsNull returns empty for soft-deleted entities
        UUID id = UUID.randomUUID();
        given(repository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.existsEntity("restore", id))
            .isInstanceOf(EntityNotFoundException.class);
    }

    // --- addHateoas ---

    @Test
    void addHateoas_returnsResponseWithSelfLink() {
        UUID id = UUID.randomUUID();
        TestEntity entity = new TestEntity();
        ReflectionTestUtils.setField(entity, "id", id);
        TestResponse response = new TestResponse();
        given(mapper.toResponse(entity)).willReturn(response);

        TestResponse result = service.addHateoas(entity);

        assertThat(result.getLink("self")).isPresent();
        String href = result.getRequiredLink("self").getHref();
        assertThat(href)
            .contains("testEntity")
            .contains(id.toString());
    }

    @Test
    void addHateoas_selfLinkUsesLowercaseEntityName() {
        UUID id = UUID.randomUUID();
        TestEntity entity = new TestEntity();
        ReflectionTestUtils.setField(entity, "id", id);
        given(mapper.toResponse(entity)).willReturn(new TestResponse());

        TestResponse result = service.addHateoas(entity);

        // entity class is "TestEntity", self URI segment must be "testEntity"
        assertThat(result.getRequiredLink("self").getHref()).contains("/testEntity/");
    }

    // --- fixtures ---

    static class TestEntity extends GenericAuditEntity {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    record TestRequest(UUID id) implements DTOIdentifiable<UUID> {}

    static class TestResponse extends RepresentationModel<TestResponse> {}
}
