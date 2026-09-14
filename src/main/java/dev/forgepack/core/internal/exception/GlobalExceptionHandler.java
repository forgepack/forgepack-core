package dev.forgepack.core.internal.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;

// import org.springframework.security.core.AuthenticationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for REST controllers.
 *
 * <p>This class centralizes exception handling across the entire application,
 * providing standardized API error responses using {@link ApiError}.</p>
 *
 * <p>It extends {@link ResponseEntityExceptionHandler} to leverage Spring's
 * default exception handling mechanisms and override specific behaviors,
 * such as validation error handling.</p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *     <li>Intercept application and framework exceptions</li>
 *     <li>Convert exceptions into structured {@link ApiError} responses</li>
 *     <li>Standardize HTTP status codes and error messages</li>
 *     <li>Handle validation errors triggered by {@code @Valid}</li>
 * </ul>
 *
 * <h3>Handled scenarios</h3>
 * <ul>
 *     <li>Authentication failures</li>
 *     <li>Validation errors (Bean Validation)</li>
 *     <li>Unhandled exceptions (fallback)</li>
 * </ul>
 *
 * <p>This class is automatically detected by Spring via {@link RestControllerAdvice}.</p>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 *
 * @see ApiError
 * @see ValidationError
 * @see RestControllerAdvice
 * @see ResponseEntityExceptionHandler
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnMissingBean(GlobalExceptionHandler.class)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Builds a standardized {@link ApiError} response with a single validation error.
     *
     * <p>This is a helper method used internally to reduce duplication when
     * constructing error responses for specific exception handlers.</p>
     *
     * @param status HTTP status to be returned
     * @param title high-level error message
     * @param field field or context associated with the error
     * @param message detailed error message
     * @param request current HTTP request
     * @return {@link ResponseEntity} containing the {@link ApiError}
     */
    private ResponseEntity<ApiError> buildApiError(HttpStatus status, String title, String field, String message, HttpServletRequest request) {
        List<ValidationError> validationErrors = List.of(new ValidationError(field, null, message));
        ApiError apiError = new ApiError(status, title, request.getRequestURI(), validationErrors);
        return new ResponseEntity<>(apiError, status);
    }

    /**
     * Handles authentication-related exceptions.
     *
     * @param exception the authentication exception thrown
     * @param request current HTTP request
     * @return standardized error response with HTTP 401 (Unauthorized)
     */
    // @ExceptionHandler(AuthenticationException.class)
    // public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException exception, HttpServletRequest request) {
    //     return buildApiError(HttpStatus.UNAUTHORIZED, "Authentication failed", "Authentication", exception.getMessage(), request);
    // }
    /**
     * Handles all uncaught exceptions.
     *
     * <p>This method acts as a fallback for any exception not explicitly handled
     * by other {@code @ExceptionHandler} methods.</p>
     *
     * @param exception the exception thrown
     * @param request current HTTP request
     * @return standardized error response with HTTP 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUncaughtExceptions(Exception exception, HttpServletRequest request) {
        return buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", "globalError", exception.getMessage(), request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {
        return buildApiError(HttpStatus.NOT_FOUND, "Resource not found", "id", ex.getMessage(), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        return buildApiError(HttpStatus.BAD_REQUEST, "Constraint violation", "id", ex.getMessage(), request);
    }

    /**
     * Handles validation errors triggered by {@code @Valid}.
     *
     * <p>This method overrides the default behavior from
     * {@link ResponseEntityExceptionHandler} to provide a custom
     * {@link ApiError} response containing detailed field and global errors.</p>
     *
     * @param ex exception containing validation errors
     * @param httpHeaders HTTP headers
     * @param httpStatusCode HTTP status code
     * @param webRequest current web request
     * @return standardized error response with HTTP 400 (Bad Request)
     */
    @Override @NullMarked
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders httpHeaders,
            HttpStatusCode httpStatusCode,
            WebRequest webRequest) {
        List<ValidationError> validationErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.add(new ValidationError(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()))
        );
        ex.getBindingResult().getGlobalErrors().forEach(error ->
                validationErrors.add(new ValidationError(
                        error.getObjectName(),
                        null,
                        error.getDefaultMessage()))
        );
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                webRequest.getDescription(false),
                validationErrors
        );
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    /**
     * Standardizes the response body for every exception handled internally by
     * {@link ResponseEntityExceptionHandler} (e.g. malformed request body,
     * unsupported HTTP method, missing parameters, unsupported media type, etc.).
     *
     * <p>Without this override, those exceptions bypass {@link #handleAllUncaughtExceptions}
     * entirely — Spring dispatches to the parent class's more specific internal handlers
     * before falling back to a generic {@code Exception.class} handler — and the client
     * receives Spring's default error body instead of {@link ApiError}.</p>
     *
     * @param ex the exception thrown
     * @param body the body prepared by the internal handler (discarded in favor of ApiError)
     * @param headers HTTP headers
     * @param statusCode HTTP status code
     * @param request current web request
     * @return standardized {@link ApiError} response
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.valueOf(statusCode.value()),
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
                request.getDescription(false)
        );
        return new ResponseEntity<>(apiError, statusCode);
    }
}