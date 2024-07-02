package ru.practicum.shareit.user.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {
    @Autowired
    UserService userService;

    @Test
    void updateUserTest() {
        User user = new User(null, "name", "email@email.ru");
        int createdUserId = userService.addUser(user).getId();
        user.setId(createdUserId);
        user.setName("changedName");
        user.setEmail("changedEmail@email.ru");
        User updatedUser = userService.updateUser(user);
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
    }
}
