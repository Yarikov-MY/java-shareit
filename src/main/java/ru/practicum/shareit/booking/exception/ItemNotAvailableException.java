package ru.practicum.shareit.booking.exception;

public class ItemNotAvailableException extends IllegalArgumentException {

    public ItemNotAvailableException(int itemId) {
        super("Вещь с id= " + itemId + "недоступна!");
    }
}