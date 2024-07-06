package ru.practicum.shareit.item.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Integer itemId) {
        super("Вещь с id=" + itemId + " не найдена!");
    }
}
