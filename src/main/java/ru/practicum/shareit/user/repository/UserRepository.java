package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    public User addUser(User user);

    public User updateUser(User user);

    public void deleteUser(Integer id);

    public List<User> getAllUsers();

    public User getUserById(Integer id);
}