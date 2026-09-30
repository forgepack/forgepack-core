package dev.forgepack.core.internal.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

import java.lang.reflect.Method;
import java.util.Collections;

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

    @Test
    void uncaughtException_returnsInternalServerError() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/boom");
        Exception exception = new RuntimeException("boom");

        var response = handler.handleAllUncaughtExceptions(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getValidationErrors())
            .extracting(ValidationError::getMessage)
            .contains("boom");
        assertThat(response.getBody().getPath()).isEqualTo("/boom");
    }

    @Test
    void constraintViolation_returnsBadRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/constraint");
        ConstraintViolationException exception = new ConstraintViolationException("invalid value", Collections.emptySet());

        var response = handler.handleConstraintViolation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Constraint violation");
        assertThat(response.getBody().getPath()).isEqualTo("/constraint");
    }

    @Test
    void methodArgumentNotValid_collectsFieldAndGlobalErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "name", "rejected", false, null, null, "must not be blank"));
        bindingResult.addError(new ObjectError("target", "global constraint violated"));
        Method method = getClass().getDeclaredMethod("dummyTarget", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/validate");
        ServletWebRequest webRequest = new ServletWebRequest(request);

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                exception, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError body = (ApiError) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getValidationErrors())
            .extracting(ValidationError::getField)
            .contains("name", "target");
    }

    @Test
    void exceptionInternal_buildsApiErrorFromStatusAndMessage() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal");
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Exception exception = new IllegalStateException("bad state");

        ResponseEntity<Object> response = handler.handleExceptionInternal(
                exception, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiError body = (ApiError) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo("bad state");
    }

    @SuppressWarnings("unused")
    private void dummyTarget(String value) {}
}
