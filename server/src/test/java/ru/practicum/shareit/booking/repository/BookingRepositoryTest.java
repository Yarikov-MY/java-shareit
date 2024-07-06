package ru.practicum.shareit.booking.repository;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;

    private final EasyRandom generator = new EasyRandom();
    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void beforeEach() {
        owner = generator.nextObject(User.class);
        owner.setId(null);
        owner = testEntityManager.persist(owner);
        booker = generator.nextObject(User.class);
        booker.setId(null);
        booker = testEntityManager.persist(booker);
        item = new Item(null, generator.nextObject(String.class), generator.nextObject(String.class), true, owner, null, null);
        item = testEntityManager.persist(item);
    }

    private Booking buildBooking(LocalDateTime start, LocalDateTime end, Status status) {
        Booking booking = new Booking(null, start, end, item, booker, status);
        return bookingRepository.save(booking);
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDescTest() {
        buildBooking(LocalDateTime.now().plusMinutes(2), LocalDateTime.now().plusHours(1), Status.WAITING);
        Booking savedBooking = buildBooking(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1), Status.WAITING);
        testEntityManager.flush();
        List<Booking> foundBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(booker.getId(), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDescTest() {
        buildBooking(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1), Status.WAITING);
        Booking savedBooking = buildBooking(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(1), Status.WAITING);
        testEntityManager.flush();
        List<Booking> foundBookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndBeforeOrderByStartDescTest() {
        buildBooking(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1), Status.WAITING);
        Booking savedBooking = buildBooking(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusMinutes(1), Status.WAITING);
        testEntityManager.flush();
        List<Booking> foundBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());
    }

    @Test
    void findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDescTest() {
        buildBooking(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(1), Status.WAITING);
        Booking savedBooking = buildBooking(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusMinutes(1), Status.WAITING);
        testEntityManager.flush();
        List<Booking> foundBookings = bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(List.of(item.getId()), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());

    }

    @Test
    void findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartDescTest() {
        buildBooking(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1), Status.WAITING);
        Booking savedBooking = buildBooking(LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(1), Status.WAITING);
        testEntityManager.flush();
        List<Booking> foundBookings = bookingRepository.findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartDesc(List.of(item.getId()), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());
    }

    @Test
    void findAllByItemIdInAndStartAfterOrderByStartDescTest() {
        buildBooking(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusHours(1), Status.WAITING);
        Booking savedBooking = buildBooking(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusHours(1), Status.WAITING);
        testEntityManager.flush();
        List<Booking> foundBookings = bookingRepository.findAllByItemIdInAndStartAfterOrderByStartDesc(List.of(item.getId()), LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());

    }

    @Test
    void findAllByItemIdAndStartBetweenAndEndBetweenTest() {
        buildBooking(LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1).minusMinutes(1), Status.WAITING);
        Booking savedBooking = buildBooking(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(1), Status.WAITING);
        testEntityManager.flush();
        List<Booking> foundBookings = bookingRepository.findAllByItemIdAndStartBetweenAndEndBetween(item.getId(), LocalDateTime.now().minusHours(1), LocalDateTime.now().plusDays(1));
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());
    }

    @Test
    void findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotInTest() {
        buildBooking(LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), Status.REJECTED);
        Booking savedBooking = buildBooking(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusDays(1), Status.WAITING);
        testEntityManager.flush();
        List<Booking> foundBookings = bookingRepository.findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(List.of(item.getId()), owner.getId(), LocalDateTime.now(), List.of(Status.REJECTED));
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());

    }

    @Test
    void findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotInTest() {
        buildBooking(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusDays(1), Status.REJECTED);
        Booking savedBooking = buildBooking(LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusDays(1), Status.WAITING);
        testEntityManager.flush();
        List<Booking> foundBookings = bookingRepository.findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(List.of(item.getId()), owner.getId(), LocalDateTime.now(), List.of(Status.REJECTED));
        assertEquals(1, foundBookings.size());
        assertEquals(savedBooking.getId(), foundBookings.get(0).getId());
    }

}
