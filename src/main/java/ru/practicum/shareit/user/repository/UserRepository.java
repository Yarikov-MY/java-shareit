package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Integer id);

    List<User> getAllUsers();

    User getUserById(Integer id);
}