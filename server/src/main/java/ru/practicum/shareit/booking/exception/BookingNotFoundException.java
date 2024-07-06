package ru.practicum.shareit.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Integer bookingId) {
        super("Бронирование с id=" + bookingId + " не найдено!");
    }

    public BookingNotFoundException() {
        super("Бронирования не найдены!");
    }
}
