package ru.practicum.shareit.error.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ForbiddenExceptionTest {
    @Test
    void forbiddenException() {
        assertDoesNotThrow(() -> new ForbiddenException(""));
    }
}
