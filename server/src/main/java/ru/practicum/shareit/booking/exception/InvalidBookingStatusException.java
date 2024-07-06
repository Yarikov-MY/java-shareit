package ru.practicum.shareit.booking.exception;

public class InvalidBookingStatusException extends IllegalArgumentException {
    public InvalidBookingStatusException(String message) {
        super(message);
    }
}
