package ru.practicum.shareit.user.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(final String message) {
        super(message);
    }
}
