package ru.practicum.shareit.request.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(Integer requestId) {
        super("Запрос на вещь с id=" + requestId + " не найдена!");
    }
}
