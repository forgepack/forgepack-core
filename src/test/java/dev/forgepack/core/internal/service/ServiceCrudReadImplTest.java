package dev.forgepack.core.internal.service;

import dev.forgepack.core.api.mapper.Mapper;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.repository.RepositoryCrud;
import dev.forgepack.core.internal.model.EntityCrud;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCrudReadImplTest {

    @Mock RepositoryCrud<TestEntity> repository;
    @Mock Mapper<TestEntity, TestRequest, TestResponse> mapper;

    TestService service;
    Page<TestEntity> emptyPage;

    @BeforeEach
    void setUp() {
        service = new TestService(repository, mapper);
        emptyPage = new PageImpl<>(Collections.emptyList());
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
    void findAll_idField_validUuid_callsFindById() {
        UUID uuid = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        given(repository.findById(uuid, pageable)).willReturn(emptyPage);

        service.findAll(pageable, uuid.toString(), TestEntity.class);

        then(repository).should().findById(uuid, pageable);
        then(repository).should(never()).findAll(any(Pageable.class));
        then(repository).should(never()).findAll(any(Example.class), any(Pageable.class));
    }

    @Test
    void findAll_idField_invalidUuid_fallsBackToFindAll() {
        // IllegalArgumentException from UUID.fromString → reflection fails for "id" → fallback
        Pageable pageable = PageRequest.of(0, 10);
        given(repository.findAll(pageable)).willReturn(emptyPage);

        service.findAll(pageable, "not-a-valid-uuid", TestEntity.class);

        then(repository).should().findAll(pageable);
        then(repository).should(never()).findById(any(UUID.class), any(Pageable.class));
    }

    @Test
    void findAll_namedField_withValue_callsFindAllWithExample() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name"));
        given(repository.findAll(any(Example.class), any(Pageable.class))).willReturn(emptyPage);

        service.findAll(pageable, "test", TestEntity.class);

        then(repository).should().findAll(any(Example.class), any(Pageable.class));
        then(repository).should(never()).findAll(any(Pageable.class));
    }

    @Test
    void findAll_nonExistentField_fallsBackToFindAll() {
        // ReflectionUtils.findField returns null → NPE on field.getType() → fallback
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nonExistentField"));
        given(repository.findAll(pageable)).willReturn(emptyPage);

        service.findAll(pageable, "value", TestEntity.class);

        then(repository).should().findAll(pageable);
        then(repository).should(never()).findAll(any(Example.class), any(Pageable.class));
    }

    @Test
    void findAll_noValue_fallsBackToFindAll() {
        // StringUtils.hasText(null) = false → skips UUID branch; setId absent → fallback
        Pageable pageable = PageRequest.of(0, 10);
        given(repository.findAll(pageable)).willReturn(emptyPage);

        service.findAll(pageable, null, TestEntity.class);

        then(repository).should().findAll(pageable);
        then(repository).should(never()).findById(any(UUID.class), any(Pageable.class));
        then(repository).should(never()).findAll(any(Example.class), any(Pageable.class));
    }

    // --- fixtures ---

    static class TestEntity extends EntityCrud {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    record TestRequest(UUID id) implements DTOIdentifiable<UUID> {}

    static class TestResponse extends RepresentationModel<TestResponse> {}

    static class TestService extends ServiceCrudReadImpl<TestEntity, TestRequest, TestResponse> {
        TestService(RepositoryCrud<TestEntity> repo, Mapper<TestEntity, TestRequest, TestResponse> mapper) {
            super(TestEntity.class, repo, mapper);
        }
    }
}
