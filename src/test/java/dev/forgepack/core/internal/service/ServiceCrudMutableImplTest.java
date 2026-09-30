package dev.forgepack.core.internal.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.repository.RepositoryCrud;
import dev.forgepack.core.api.model.EntityCrud;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ServiceCrudMutableImplTest {

    @Mock RepositoryCrud<TestEntity> repository;
    @Mock Mapper<TestEntity, TestRequest, TestResponse> mapper;

    TestService service;

    @BeforeEach
    void setUp() {
        service = new TestService(repository, mapper);
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

    @Test
    void create_savesMappedEntityAndReturnsResponseWithHateoas() {
        TestRequest request = new TestRequest(null);
        TestEntity mapped = new TestEntity();
        ReflectionTestUtils.setField(mapped, "id", UUID.randomUUID());
        TestResponse response = new TestResponse();
        given(mapper.toEntity(request)).willReturn(mapped);
        given(repository.save(mapped)).willReturn(mapped);
        given(mapper.toResponse(mapped)).willReturn(response);

        TestResponse result = service.create(request);

        then(repository).should().save(mapped);
        assertThat(result).isSameAs(response);
        assertThat(result.getLink("self")).isPresent();
    }

    @Test
    void update_existingEntity_appliesChangesAndReturnsResponse() {
        UUID id = UUID.randomUUID();
        TestEntity existing = new TestEntity();
        ReflectionTestUtils.setField(existing, "id", id);
        TestRequest request = new TestRequest(id);
        TestResponse response = new TestResponse();
        given(repository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.of(existing));
        given(repository.save(existing)).willReturn(existing);
        given(mapper.toResponse(existing)).willReturn(response);

        TestResponse result = service.update(id, request);

        then(mapper).should().updateEntity(request, existing);
        then(repository).should().save(existing);
        assertThat(result).isSameAs(response);
    }

    @Test
    void update_nonExistentEntity_throwsEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        TestRequest request = new TestRequest(id);
        given(repository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("update");
    }

    // --- fixtures ---

    static class TestEntity extends EntityCrud {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    record TestRequest(UUID id) implements DTOIdentifiable<UUID> {}

    static class TestResponse extends RepresentationModel<TestResponse> {}

    static class TestService extends ServiceCrudMutableImpl<TestEntity, TestRequest, TestResponse> {
        TestService(RepositoryCrud<TestEntity> repo, Mapper<TestEntity, TestRequest, TestResponse> mapper) {
            super(TestEntity.class, repo, mapper);
        }
    }
}
