package dev.forgepack.core.internal.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a standardized error response returned by the API.
 *
 * <p>This class encapsulates error details generated during request processing,
 * including HTTP status, error message, request path, timestamp, and optional
 * validation errors.</p>
 *
 * <p>It is typically used in exception handling mechanisms (e.g. global
 * exception handlers) to provide consistent and structured error responses
 * to API clients.</p>
 *
 * <h3>Structure</h3>
 * <ul>
 *     <li>{@code status} – HTTP status associated with the error</li>
 *     <li>{@code message} – human-readable error description</li>
 *     <li>{@code path} – request URI that caused the error</li>
 *     <li>{@code timestamp} – moment when the error occurred</li>
 *     <li>{@code validationErrors} – list of field-level validation errors (optional)</li>
 * </ul>
 *
 * <p>The {@code validationErrors} field is typically populated when the error
 * originates from input validation failures.</p>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 *
 * @see ValidationError
 * @see org.springframework.http.HttpStatus
 */
public class ApiError {
    private final HttpStatus status;
    private final String message;
    private final String path;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;
    private List<ValidationError> validationErrors;

    /**
     * Creates an error response including validation errors.
     *
     * @param status HTTP status
     * @param message error message
     * @param path request path
     * @param validationErrors list of validation errors
     */
    public ApiError(HttpStatus status, String message, String path, List<ValidationError> validationErrors) {
        this(status, message, path);
        this.validationErrors = validationErrors;
    }
    /**
     * Creates a basic error response without validation details.
     *
     * <p>The timestamp is automatically set to the current time.</p>
     *
     * @param status HTTP status
     * @param message error message
     * @param path request path
     */
    public ApiError(HttpStatus status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
    /**
     * Creates a fully customized error response.
     *
     * @param status HTTP status
     * @param message error message
     * @param path request path
     * @param timestamp timestamp of the error
     * @param validationErrors list of validation errors
     */
    public ApiError(HttpStatus status, String message, String path, LocalDateTime timestamp, List<ValidationError> validationErrors) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.validationErrors = validationErrors;
    }

    public HttpStatus getStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
    public String getPath() {
        return path;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }
}
