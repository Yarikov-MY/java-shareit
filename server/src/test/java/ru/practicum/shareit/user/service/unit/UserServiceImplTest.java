package ru.practicum.shareit.user.service.unit;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceImplTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = new UserServiceImpl(userRepository);
    private final EasyRandom generator = new EasyRandom();

    @Test
    void addUserTest() {
        User user = generator.nextObject(User.class);
        when(userRepository.save(any(User.class))).thenReturn(user);
        User createdUser = userService.addUser(user);
        assertEquals(user, createdUser);
    }

    @Test
    void updateUserTest() {
        User userForUpdate = generator.nextObject(User.class);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(userForUpdate));
        when(userRepository.save(any(User.class))).thenReturn(userForUpdate);
        User updatedUser = userService.updateUser(generator.nextObject(User.class));
        assertEquals(userForUpdate, updatedUser);
    }

    @Test
    void getAllUsersTest() {
        List<User> users = generator.objects(User.class, 2).collect(Collectors.toList());
        when(userRepository.findAll()).thenReturn(users);
        List<User> foundUsers = userService.getAllUsers();
        assertEquals(users, foundUsers);
    }

    @Test
    void deleteUserTest() {
        User user = generator.nextObject(User.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));
        assertDoesNotThrow(() -> userService.deleteUser(user.getId()));
    }

    @Test
    void getUserByIdTest() {
        User user = generator.nextObject(User.class);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User foundUser = userService.getUserById(user.getId());
        assertEquals(user, foundUser);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdateNotExistedUserTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        User user = generator.nextObject(User.class);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenTryToGetNotExistedUserTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        Integer userId = generator.nextInt();
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenTryToDeleteNotExistedUserTest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        Integer userId = generator.nextInt();
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }
}
