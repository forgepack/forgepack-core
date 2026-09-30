package dev.forgepack.core.internal.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationErrorTest {

    @Test
    void gettersReturnConstructorValues() {
        ValidationError error = new ValidationError("field", "rejected", "message");

        assertThat(error.getField()).isEqualTo("field");
        assertThat(error.getRejectedValue()).isEqualTo("rejected");
        assertThat(error.getMessage()).isEqualTo("message");
    }
}
