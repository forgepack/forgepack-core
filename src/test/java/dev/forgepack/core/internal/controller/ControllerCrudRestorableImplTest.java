package dev.forgepack.core.internal.controller;

import dev.forgepack.core.api.model.EntityCrud;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.service.ServiceCrudRestorable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ControllerCrudRestorableImplTest {

    @Mock ServiceCrudRestorable<TestEntity, TestRequest, TestResponse> service;

    TestController controller;

    @BeforeEach
    void setUp() {
        controller = new TestController(service);
    }

    @Test
    void softDelete_returnsNoContent() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> result = controller.softDelete(id);

        then(service).should().softDelete(id);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void hardDelete_returnsNoContent() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> result = controller.hardDelete(id);

        then(service).should().hardDelete(id);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void restore_returnsAcceptedWithBody() {
        UUID id = UUID.randomUUID();
        TestResponse response = new TestResponse(id);
        given(service.restore(id)).willReturn(response);

        ResponseEntity<TestResponse> result = controller.restore(id);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(result.getBody()).isSameAs(response);
    }

    // --- fixtures ---

    static class TestEntity extends EntityCrud {}

    record TestRequest(UUID id) implements DTOIdentifiable<UUID> {}

    record TestResponse(UUID id) implements DTOIdentifiable<UUID> {}

    static class TestController extends ControllerCrudRestorableImpl<TestEntity, TestRequest, TestResponse> {
        TestController(ServiceCrudRestorable<TestEntity, TestRequest, TestResponse> service) {
            super(TestEntity.class, service);
        }
    }
}
