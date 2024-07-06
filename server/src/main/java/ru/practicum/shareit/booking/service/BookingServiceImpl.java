package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.InvalidBookingStatusException;
import ru.practicum.shareit.booking.exception.ItemNotAvailableException;
import ru.practicum.shareit.booking.exception.OwnerCantBookingItems;
import ru.practicum.shareit.booking.exception.UserNotOwnerOrCreator;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Booking addBooking(Booking booking, Integer itemId, Integer bookerId)
            throws ItemNotFoundException, UserNotFoundException, ItemNotAvailableException {
        if (booking.getStart().equals(booking.getEnd()) || booking.getStart().isAfter(booking.getEnd())) {
            throw new IllegalArgumentException(
                    "Некорректное время начала/окончания бронирования!"
            );
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(itemId);
        }
        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new OwnerCantBookingItems();
        }
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new UserNotFoundException(bookerId));
        List<Booking> alreadyAddedBookings = bookingRepository
                .findAllByItemIdAndStartBetweenAndEndBetween(item.getId(), booking.getStart(), booking.getEnd());
        if (!alreadyAddedBookings.isEmpty()) {
            throw new ItemNotAvailableException(itemId);
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking approveBooking(Integer bookingId, Integer ownerId, Boolean isApproved)
            throws BookingNotFoundException, UserNotOwnerOrCreator, InvalidBookingStatusException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            throw new UserNotOwnerOrCreator();
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new InvalidBookingStatusException("Статус бронирования " + bookingId + " отличен от " + Status.WAITING);
        }
        Status status;
        if (isApproved) {
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Integer bookingId, Integer userId) throws BookingNotFoundException, UserNotOwnerOrCreator {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
        if (!Objects.equals(booking.getBooker().getId(), userId) && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new UserNotOwnerOrCreator();
        }
        return booking;
    }

    @Override
    public List<Booking> getAllBookingsOfUserByState(Integer bookerId, State state, Integer from, Integer size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Размер должен быть больше нуля!");
        }
        List<Booking> allBookings;
        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case CURRENT:
                allBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, currentLocalDateTime, currentLocalDateTime, pageable);
                break;
            case FUTURE:
                allBookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        bookerId, currentLocalDateTime, pageable);
                break;
            case PAST:
                allBookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(
                        bookerId, currentLocalDateTime, currentLocalDateTime, pageable);
                break;
            case WAITING:
                allBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId, Status.WAITING, pageable);
                break;
            case REJECTED:
                allBookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        bookerId, Status.REJECTED, pageable);
                break;
            default:
                allBookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable);
        }
        if (allBookings.isEmpty()) {
            throw new BookingNotFoundException();
        }
        return allBookings;
    }

    @Override
    public List<Booking> getAllBookingsOfUserItems(Integer ownerId, State state, Integer from, Integer size) throws BookingNotFoundException {
        if (size <= 0) {
            throw new IllegalArgumentException("Размер должен быть больше нуля!");
        }
        List<Booking> allBookings;
        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        List<Integer> itemIds = itemRepository.findByOwnerId(ownerId, Pageable.unpaged())
                .stream().map(Item::getId).collect(Collectors.toList());
        if (itemIds.isEmpty()) {
            throw new BookingNotFoundException();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case CURRENT:
                allBookings = bookingRepository.findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
                        itemIds, currentLocalDateTime, currentLocalDateTime, pageable);
                break;
            case FUTURE:
                allBookings = bookingRepository.findAllByItemIdInAndStartAfterOrderByStartDesc(
                        itemIds, currentLocalDateTime, pageable);
                break;
            case PAST:
                allBookings = bookingRepository.findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartDesc(
                        itemIds, currentLocalDateTime, currentLocalDateTime, pageable);
                break;
            case WAITING:
                allBookings = bookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(
                        itemIds, Status.WAITING, pageable);
                break;
            case REJECTED:
                allBookings = bookingRepository.findAllByItemIdInAndStatusOrderByStartDesc(
                        itemIds, Status.REJECTED, pageable);
                break;
            default:
                allBookings = bookingRepository.findAllByItemIdInOrderByStartDesc(itemIds, pageable);
        }
        return allBookings;
    }
}
