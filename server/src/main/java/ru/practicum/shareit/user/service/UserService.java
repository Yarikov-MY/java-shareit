package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    User updateUser(User user) throws UserNotFoundException;

    void deleteUser(Integer userId) throws UserNotFoundException;

    List<User> getAllUsers();

    User getUserById(Integer id) throws UserNotFoundException;
}
