package ru.practicum.shareit.booking.service.integration;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Transactional
public class BookingServiceIntegrationTest {
    @Autowired
    private EntityManager entityManager;

    private final EasyRandom generator = new EasyRandom();
    @Autowired
    private BookingService bookingService;

    private User booker;
    private Item item;

    @BeforeEach
    void beforeEach() {
        User owner = generator.nextObject(User.class);
        owner.setId(null);
        entityManager.persist(owner);
        booker = generator.nextObject(User.class);
        booker.setId(null);
        entityManager.persist(booker);
        item = new Item(null, generator.nextObject(String.class), generator.nextObject(String.class), true, owner, null, null);
        entityManager.persist(item);
    }

    @Test
    void addBookingTest() {
        Booking booking = new Booking(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), item, booker, Status.WAITING);
        assertDoesNotThrow(() -> bookingService.addBooking(booking, item.getId(), booker.getId()));
    }
}
