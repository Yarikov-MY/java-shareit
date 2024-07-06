package ru.practicum.shareit.item.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ExceptionsTest {
    @Test
    void itemNotFoundException() {
        assertDoesNotThrow(() -> new ItemNotFoundException(1));
    }
}
