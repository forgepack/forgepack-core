package dev.forgepack.core.internal.controller;

import dev.forgepack.core.api.model.EntityCrud;
import dev.forgepack.core.api.payload.DTOIdentifiable;
import dev.forgepack.core.api.service.ServiceCrudMutable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ControllerCrudMutableImplTest {

    @Mock ServiceCrudMutable<TestEntity, TestRequest, TestResponse> service;

    TestController controller;

    @BeforeEach
    void setUp() {
        controller = new TestController(service);
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setScheme("http");
        httpRequest.setServerName("localhost");
        httpRequest.setServerPort(8080);
        httpRequest.setRequestURI("/test-entities");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void create_returnsCreatedWithLocationHeader() {
        UUID id = UUID.randomUUID();
        TestRequest request = new TestRequest(null);
        TestResponse response = new TestResponse(id);
        given(service.create(request)).willReturn(response);

        ResponseEntity<TestResponse> result = controller.create(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getHeaders().getLocation()).isNotNull();
        assertThat(result.getHeaders().getLocation().toString()).contains(id.toString());
        assertThat(result.getBody()).isSameAs(response);
    }

    @Test
    void update_returnsOkWithBody() {
        UUID id = UUID.randomUUID();
        TestRequest request = new TestRequest(id);
        TestResponse response = new TestResponse(id);
        given(service.update(id, request)).willReturn(response);

        ResponseEntity<TestResponse> result = controller.update(id, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(response);
    }

    // --- fixtures ---

    static class TestEntity extends EntityCrud {}

    record TestRequest(UUID id) implements DTOIdentifiable<UUID> {}

    record TestResponse(UUID id) implements DTOIdentifiable<UUID> {}

    static class TestController extends ControllerCrudMutableImpl<TestEntity, TestRequest, TestResponse> {
        TestController(ServiceCrudMutable<TestEntity, TestRequest, TestResponse> service) {
            super(TestEntity.class, service);
        }
    }
}
