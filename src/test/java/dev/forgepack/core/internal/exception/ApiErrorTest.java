package dev.forgepack.core.internal.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorTest {

    @Test
    void threeArgConstructor_setsTimestampAndNullValidationErrors() {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, "msg", "/path");

        assertThat(error.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error.getMessage()).isEqualTo("msg");
        assertThat(error.getPath()).isEqualTo("/path");
        assertThat(error.getTimestamp()).isNotNull();
        assertThat(error.getValidationErrors()).isNull();
    }

    @Test
    void fourArgConstructor_setsValidationErrors() {
        List<ValidationError> errors = List.of(new ValidationError("field", "value", "message"));

        ApiError error = new ApiError(HttpStatus.NOT_FOUND, "msg", "/path", errors);

        assertThat(error.getValidationErrors()).isEqualTo(errors);
        assertThat(error.getTimestamp()).isNotNull();
    }

    @Test
    void fiveArgConstructor_setsAllFieldsExplicitly() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 0, 0);
        List<ValidationError> errors = List.of(new ValidationError("field", "value", "message"));

        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "msg", "/path", timestamp, errors);

        assertThat(error.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(error.getMessage()).isEqualTo("msg");
        assertThat(error.getPath()).isEqualTo("/path");
        assertThat(error.getTimestamp()).isEqualTo(timestamp);
        assertThat(error.getValidationErrors()).isEqualTo(errors);
    }
}
