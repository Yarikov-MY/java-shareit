package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.DuplicateEmailException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        List<User> users = userRepository.getAllUsers();
        if (users.stream().anyMatch(it -> Objects.equals(it.getEmail(), user.getEmail()))) {
            throw new DuplicateEmailException("Такой email уже существует!");
        }
        return userRepository.addUser(user);
    }

    @Override
    public User updateUser(User user) {
        User userToUpdate = userRepository.getUserById(user.getId());
        if (userToUpdate != null) {
            if (user.getEmail() != null) {
                List<User> users = userRepository.getAllUsers();
                if (users
                        .stream()
                        .filter(it -> !Objects.equals(it.getId(), user.getId()))
                        .anyMatch(it -> Objects.equals(it.getEmail(), user.getEmail()))) {
                    throw new DuplicateEmailException("Такой email уже существует!");
                }
                userToUpdate.setEmail(user.getEmail());
            }
            if (user.getName() != null) {
                userToUpdate.setName(user.getName());
            }
            userRepository.updateUser(userToUpdate);
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }
        return userToUpdate;
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.getUserById(id);
    }
}
