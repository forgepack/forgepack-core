package dev.forgepack.core.internal.controller;

import dev.forgepack.core.api.model.EntityCrud;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.service.ServiceCrudRead;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ControllerCrudReadImplTest {

    @Mock ServiceCrudRead<TestEntity, TestResponse> service;

    TestController controller;

    @BeforeEach
    void setUp() {
        controller = new TestController(service);
    }

    @Test
    void findAll_delegatesToServiceAndReturnsOk() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TestResponse> page = new PageImpl<>(List.of(new TestResponse(UUID.randomUUID())));
        given(service.findAll(pageable, "value", TestEntity.class)).willReturn(page);

        ResponseEntity<Page<TestResponse>> result = controller.findAll("value", pageable);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(page);
    }

    @Test
    void findById_delegatesToServiceAndReturnsOk() {
        UUID id = UUID.randomUUID();
        TestResponse response = new TestResponse(id);
        given(service.findById(id)).willReturn(response);

        ResponseEntity<TestResponse> result = controller.findById(id);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(response);
    }

    // --- fixtures ---

    static class TestEntity extends EntityCrud {}

    record TestRequest(UUID id) implements DTOIdentifiable<UUID> {}

    record TestResponse(UUID id) implements DTOIdentifiable<UUID> {}

    static class TestController extends ControllerCrudReadImpl<TestEntity, TestRequest, TestResponse> {
        TestController(ServiceCrudRead<TestEntity, TestResponse> service) {
            super(TestEntity.class, service);
        }
    }
}
