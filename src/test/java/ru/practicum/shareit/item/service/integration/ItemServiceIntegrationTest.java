package ru.practicum.shareit.item.service.integration;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingInfo;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ItemServiceIntegrationTest {
    @Autowired
    private EntityManager entityManager;

    private final EasyRandom generator = new EasyRandom();

    @Autowired
    private ItemService itemService;
    private User booker;
    private User owner;

    @BeforeEach
    void beforeEach() {
        owner = generator.nextObject(User.class);
        owner.setId(null);
        entityManager.persist(owner);
        booker = generator.nextObject(User.class);
        booker.setId(null);
        entityManager.persist(booker);
    }

    @Test
    void getAllItemsByOwnerId() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Item item = generator.nextObject(Item.class);
            item.setId(null);
            item.setAvailable(true);
            item.setOwner(owner);
            item.setRequest(null);
            entityManager.persist(item);
            items.add(item);
        }
        Map<Integer, Item> itemsMap = items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Integer, Booking> lastBookings = new HashMap<>();
        Map<Integer, Booking> nextBookings = new HashMap<>();
        Map<Integer, Comment> comments = new HashMap<>();
        itemsMap.keySet().forEach(itemId -> {
            Booking lastBooking = new Booking(null, LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusDays(1), itemsMap.get(itemId), booker, Status.APPROVED);
            Booking nextBooking = new Booking(null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(1), itemsMap.get(itemId), booker, Status.APPROVED);
            Comment comment = generator.nextObject(Comment.class);
            comment.setId(null);
            comment.setItem(itemsMap.get(itemId));
            comment.setAuthor(booker);
            entityManager.persist(lastBooking);
            entityManager.persist(nextBooking);
            entityManager.persist(comment);
            lastBookings.put(itemId, lastBooking);
            nextBookings.put(itemId, nextBooking);
            comments.put(itemId, comment);
        });
        entityManager.flush();
        List<ItemBookingInfo> gotOwnerItems = itemService.getOwnerItems(owner.getId(), 0, 5);
        for (ItemBookingInfo itemsBookingInfo : gotOwnerItems) {
            assertEquals(itemsMap.get(itemsBookingInfo.getItem().getId()).getDescription(), itemsBookingInfo.getItem().getDescription());
            assertEquals(itemsMap.get(itemsBookingInfo.getItem().getId()).getName(), itemsBookingInfo.getItem().getName());
            assertEquals(lastBookings.get(itemsBookingInfo.getItem().getId()).getId(), itemsBookingInfo.getLastBooking().getId());
            assertEquals(nextBookings.get(itemsBookingInfo.getItem().getId()).getId(), itemsBookingInfo.getNextBooking().getId());
            assertEquals(comments.get(itemsBookingInfo.getItem().getId()).getId(), itemsBookingInfo.getComments().get(0).getId());
        }

    }
}
