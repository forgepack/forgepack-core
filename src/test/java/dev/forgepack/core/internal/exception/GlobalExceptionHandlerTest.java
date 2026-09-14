package dev.forgepack.core.internal.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void entityNotFound_returnsNotFoundApiError() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/test-entities/1");
        EntityNotFoundException exception = new EntityNotFoundException("TestEntity not found with ID 1");

        var response = handler.handleEntityNotFound(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Resource not found");
        assertThat(response.getBody().getValidationErrors())
            .extracting(ValidationError::getMessage)
            .contains("TestEntity not found with ID 1");
        assertThat(response.getBody().getPath()).isEqualTo("/test-entities/1");
    }
}
