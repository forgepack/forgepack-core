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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ServiceCrudRestorableImplTest {

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
    void softDelete_existingEntity_setsDeletedAtAndReturnsResponse() {
        UUID id = UUID.randomUUID();
        TestEntity entity = new TestEntity();
        ReflectionTestUtils.setField(entity, "id", id);
        TestResponse response = new TestResponse();
        given(repository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.of(entity));
        given(repository.save(entity)).willReturn(entity);
        given(mapper.toResponse(entity)).willReturn(response);

        TestResponse result = service.softDelete(id);

        assertThat(entity.getDeletedAt()).isNotNull();
        then(repository).should().save(entity);
        assertThat(result).isSameAs(response);
    }

    @Test
    void softDelete_nonExistentEntity_throwsEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        given(repository.findByIdAndDeletedAtIsNull(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.softDelete(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void restore_deletedEntity_clearsDeletedAtAndReturnsResponse() {
        UUID id = UUID.randomUUID();
        TestEntity entity = new TestEntity();
        ReflectionTestUtils.setField(entity, "id", id);
        entity.setDeletedAt(LocalDateTime.now());
        TestResponse response = new TestResponse();
        given(repository.findByIdAndDeletedAtIsNotNull(id)).willReturn(Optional.of(entity));
        given(repository.save(entity)).willReturn(entity);
        given(mapper.toResponse(entity)).willReturn(response);

        TestResponse result = service.restore(id);

        assertThat(entity.getDeletedAt()).isNull();
        then(repository).should().save(entity);
        assertThat(result).isSameAs(response);
    }

    @Test
    void restore_nonExistentEntity_throwsEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        given(repository.findByIdAndDeletedAtIsNotNull(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.restore(id)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void hardDelete_deletedEntity_removesItAndReturnsResponse() {
        UUID id = UUID.randomUUID();
        TestEntity entity = new TestEntity();
        ReflectionTestUtils.setField(entity, "id", id);
        entity.setDeletedAt(LocalDateTime.now());
        TestResponse response = new TestResponse();
        given(repository.findByIdAndDeletedAtIsNotNull(id)).willReturn(Optional.of(entity));
        given(mapper.toResponse(entity)).willReturn(response);

        TestResponse result = service.hardDelete(id);

        then(repository).should().delete(entity);
        assertThat(result).isSameAs(response);
    }

    @Test
    void hardDelete_nonExistentEntity_throwsEntityNotFoundException() {
        UUID id = UUID.randomUUID();
        given(repository.findByIdAndDeletedAtIsNotNull(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.hardDelete(id)).isInstanceOf(EntityNotFoundException.class);
    }

    // --- fixtures ---

    static class TestEntity extends EntityCrud {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    record TestRequest(UUID id) implements DTOIdentifiable<UUID> {}

    static class TestResponse extends RepresentationModel<TestResponse> {}

    static class TestService extends ServiceCrudRestorableImpl<TestEntity, TestRequest, TestResponse> {
        TestService(RepositoryCrud<TestEntity> repo, Mapper<TestEntity, TestRequest, TestResponse> mapper) {
            super(TestEntity.class, repo, mapper);
        }
    }
}
