package ru.practicum.shareit.booking.service.unit;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.exception.InvalidBookingStatusException;
import ru.practicum.shareit.booking.exception.UserNotOwnerOrCreator;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class BookingServiceImplTest {
    private final EasyRandom generator = new EasyRandom();
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final BookingRepository bookingRepository = mock(BookingRepository.class);

    private final BookingService bookingService = new BookingServiceImpl(
            itemRepository, userRepository, bookingRepository
    );

    private User user;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
    }

    @Test
    void addBookingTest() {
        Item item = generator.nextObject(Item.class);
        Booking booking = generator.nextObject(Booking.class);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemIdAndStartBetweenAndEndBetween(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any())).thenReturn(booking);
        Booking addedBooking = bookingService.addBooking(booking, item.getId(), user.getId());
        assertEquals(booking, addedBooking);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAddBookingWithNotExistedUserTest() {
        User user = generator.nextObject(User.class);
        when(userRepository.findById(user.getId() + 1)).thenReturn(Optional.of(generator.nextObject(User.class)));
        IllegalArgumentException iae = assertThrows(IllegalArgumentException.class,
                () -> bookingService.addBooking(generator.nextObject(Booking.class), user.getId(), generator.nextInt()));
        assertEquals("Некорректное время начала/окончания бронирования!", iae.getMessage());
    }

    @Test
    void getBookingTest() {
        Booking booking = generator.nextObject(Booking.class);
        booking.setBooker(user);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        Booking foundBooking = bookingService.getBooking(booking.getId(), user.getId());
        assertEquals(booking, foundBooking);
    }

    @Test
    void shouldThrowsUserNotOwnerOrCreatorExceptionAfterGetBookingTest() {
        Booking booking = generator.nextObject(Booking.class);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertThrows(UserNotOwnerOrCreator.class, () -> bookingService.getBooking(booking.getId(), user.getId()));
    }

    @Test
    void approveBookingTest() {
        Booking booking = generator.nextObject(Booking.class);
        booking.getItem().setOwner(user);
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Booking approvedBooking = bookingService.approveBooking(booking.getId(), user.getId(), true);
        assertEquals(Status.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void shouldThrowUserNotOwnerOrCreatorAfterApproveBooking() {
        Booking booking = generator.nextObject(Booking.class);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertThrows(UserNotOwnerOrCreator.class, () -> bookingService.approveBooking(booking.getId(), user.getId(), true));
    }

    @Test
    void shouldThrowInvalidBookingStatusExceptionAfterApproveBookingTest() {
        Booking booking = generator.nextObject(Booking.class);
        booking.getItem().setOwner(user);
        booking.setStatus(Status.CANCELED);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        assertThrows(InvalidBookingStatusException.class, () -> bookingService.approveBooking(booking.getId(), user.getId(), true));
    }

    @Test
    void getAllBookingsOfUserByCurrentStateTest() {
        List<Booking> bookings = generator.objects(Booking.class, 5).collect(Collectors.toList());
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)
        )).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(user.getId(), State.CURRENT, 0, 5);
        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserByFutureStateTest() {
        List<Booking> bookings = generator.objects(Booking.class, 5).collect(Collectors.toList());
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(user.getId(), State.FUTURE, 0, 5);
        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserByPastStateTest() {
        List<Booking> bookings = generator.objects(Booking.class, 5).collect(Collectors.toList());
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(user.getId(), State.PAST, 0, 10);
        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserByWaitingStateTest() {
        List<Booking> bookings = generator.objects(Booking.class, 5).collect(Collectors.toList());
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(), eq(Status.WAITING), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(user.getId(), State.WAITING, 0, 5);
        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserByRejectedStateTest() {
        List<Booking> bookings = generator.objects(Booking.class, 5).collect(Collectors.toList());
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(), eq(Status.REJECTED), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(user.getId(), State.REJECTED, 0, 5);
        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserByAllStateTest() {
        List<Booking> bookings = generator.objects(Booking.class, 5).collect(Collectors.toList());
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyInt(), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserByState(user.getId(), State.ALL, 0, 5);
        assertEquals(bookings, foundBookings);
    }

    private List<Booking> buildDataGetAllBookingsOfUserItems() {
        List<Item> items = generator.objects(Item.class, 5).collect(Collectors.toList());
        List<Booking> bookings = generator.objects(Booking.class, 5).collect(Collectors.toList());
        for (int i = 0; i < bookings.size(); i++) {
            items.get(i).setOwner(user);
            bookings.get(i).setItem(items.get(i));
        }
        when(itemRepository.findByOwnerId(anyInt(), any(Pageable.class))).thenReturn(items);
        return bookings;
    }

    @Test
    void getAllBookingsOfUserItemsByCurrentState() {
        List<Booking> bookings = buildDataGetAllBookingsOfUserItems();
        when(bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(user.getId(), State.CURRENT, 0, 5);
        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByFutureState() {
        List<Booking> bookings = buildDataGetAllBookingsOfUserItems();
        when(bookingRepository.findAllByItemIdInAndStartAfterOrderByStartDesc(anyList(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(user.getId(), State.FUTURE, 0, 5);
        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByPastStateTest() {
        List<Booking> bookings = buildDataGetAllBookingsOfUserItems();
        when(bookingRepository.findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartDesc(anyList(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(user.getId(), State.PAST, 0, 5);
        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByWaitingStateTest() {
        List<Booking> bookings = buildDataGetAllBookingsOfUserItems();
        when(bookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(anyList(), eq(Status.WAITING), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(user.getId(), State.WAITING, 0, 5);
        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByRejectedStateTest() {
        List<Booking> bookings = buildDataGetAllBookingsOfUserItems();
        when(bookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(anyList(), eq(Status.REJECTED), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(user.getId(), State.REJECTED, 0, 5);
        assertEquals(bookings, foundBookings);
    }

    @Test
    void getAllBookingsOfUserItemsByAllStateTest() {
        List<Booking> bookings = buildDataGetAllBookingsOfUserItems();
        when(bookingRepository.findAllByItemIdInOrderByStartDesc(anyList(), any(Pageable.class))).thenReturn(bookings);
        List<Booking> foundBookings = bookingService.getAllBookingsOfUserItems(user.getId(), State.ALL, 0, 5);
        assertEquals(bookings, foundBookings);
    }

}
