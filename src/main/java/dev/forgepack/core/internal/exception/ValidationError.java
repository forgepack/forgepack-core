package dev.forgepack.core.internal.exception;

/**
 * Represents a validation error for a specific field.
 *
 * <p>This class is a simple value object used to encapsulate validation
 * error details, typically returned in API responses when input validation
 * fails.</p>
 *
 * <p>Each instance contains the field that failed validation, the rejected
 * value, and an associated error message.</p>
 *
 * <h3>Typical usage</h3>
 * <ul>
 *     <li>Collecting validation errors from Bean Validation</li>
 *     <li>Building standardized error responses in REST APIs</li>
 *     <li>Providing detailed feedback to clients</li>
 * </ul>
 *
 * @author Marcelo Ribeiro Gadelha
 * @since 1.0
 */
public class ValidationError {
    private final String field;
    private final Object rejectedValue;
    private final String message;

    /**
     * Creates a new validation error instance.
     *
     * @param field name of the field that failed validation
     * @param rejectedValue value that was rejected
     * @param message validation error message
     */
    public ValidationError(String field, Object rejectedValue, String message) {
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    public String getField() {
        return field;
    }
    public Object getRejectedValue() {
        return rejectedValue;
    }
    public String getMessage() {
        return message;
    }
}
