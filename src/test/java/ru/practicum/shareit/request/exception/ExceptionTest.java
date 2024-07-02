package ru.practicum.shareit.request.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ExceptionTest {
    @Test
    void itemRequestNotFoundException() {
        assertDoesNotThrow(() -> new ItemRequestNotFoundException(1));
    }
}
