package ru.practicum.shareit.request.service.integration;


import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Transactional
public class ItemRequestServiceIntegrationTest {
    @Autowired
    private EntityManager entityManager;

    private final EasyRandom generator = new EasyRandom();

    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void addRequestTest() {
        User user = generator.nextObject(User.class);
        user.setId(null);
        entityManager.persist(user);
        ItemRequest itemRequest = new ItemRequest(null, generator.nextObject(String.class), null, LocalDateTime.now(), null);
        assertDoesNotThrow(
                () -> itemRequestService.addRequest(itemRequest, user.getId())
        );
    }
}
