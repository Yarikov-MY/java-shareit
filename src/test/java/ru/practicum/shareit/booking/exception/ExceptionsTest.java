package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ExceptionsTest {
    @Test
    void bookingNotFoundException() {
        assertDoesNotThrow(() -> new BookingNotFoundException(1));
    }

    @Test
    void itemNotAvailableException() {
        assertDoesNotThrow(() -> new ItemNotAvailableException(1));
    }

    @Test
    void ownerCantBookingItems() {
        assertDoesNotThrow(OwnerCantBookingItems::new);
    }

    @Test
    void unsupportedStatusException() {
        assertDoesNotThrow(UnsupportedStatusException::new);
    }

    @Test
    void userNotOwnerOrCreator() {
        assertDoesNotThrow(UserNotOwnerOrCreator::new);
    }
}
