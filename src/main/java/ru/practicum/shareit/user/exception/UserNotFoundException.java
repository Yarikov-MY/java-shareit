package ru.practicum.shareit.user.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Integer userId) {
        super("Пользователь с id=" + userId + " не найден!");
    }
}
