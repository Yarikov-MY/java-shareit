package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingInfo;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    private static final List<Status> NEGATIVE_BOOKING_STATUSES =
            List.of(Status.CANCELED, Status.REJECTED);

    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository, CommentRepository commentRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    @Transactional
    public Item addItem(Item item, Integer ownerId) throws UserNotFoundException {
        User itemOwner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ownerId));
        item.setOwner(itemOwner);
        if (item.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(item.getRequestId()).orElseThrow(() -> new ItemRequestNotFoundException(item.getRequestId()));
            item.setRequest(itemRequest);
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Item item, Integer ownerId) throws UserNotFoundException, ItemNotFoundException, ForbiddenException {
        User itemOwner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ownerId));
        Item itemToUpdate = itemRepository.findById(item.getId()).orElseThrow(() -> new ItemNotFoundException(item.getId()));
        if (Objects.equals(itemToUpdate.getOwner().getId(), itemOwner.getId())) {
            if (item.getName() != null) {
                itemToUpdate.setName(item.getName());
            }
            if (item.getDescription() != null) {
                itemToUpdate.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemToUpdate.setAvailable(item.getAvailable());
            }
            return itemRepository.save(itemToUpdate);
        } else {
            throw new ForbiddenException("Попытка изменить чужую вещь!");
        }
    }

    @Override
    @Transactional
    public ItemBookingInfo getItemByIdAndUserId(Integer itemId, Integer userId) throws UserNotFoundException, ItemNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        if (!Objects.equals(user.getId(), item.getOwner().getId())) {
            return new ItemBookingInfo(item, comments);
        }
        LocalDateTime nowDateTime = LocalDateTime.now();
        Booking lastBooking = bookingRepository
                .findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(
                        List.of(item.getId()), user.getId(), nowDateTime, NEGATIVE_BOOKING_STATUSES)
                .stream().findFirst().orElse(null);
        Booking nextBooking = bookingRepository
                .findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(
                        List.of(item.getId()), user.getId(), nowDateTime, NEGATIVE_BOOKING_STATUSES
                ).stream().findFirst().orElse(null);
        return new ItemBookingInfo(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional
    public List<ItemBookingInfo> getOwnerItems(Integer ownerId, Integer from, Integer size) throws UserNotFoundException {
        if (size <= 0) {
            throw new IllegalArgumentException("Размер должен быть больше нуля!");
        }
        User user = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException(ownerId));
        List<Item> userItems = itemRepository.findByOwnerId(user.getId(), PageRequest.of(from / size, size));
        if (userItems.isEmpty()) {
            return new ArrayList<>();
        }
        LocalDateTime nowDateTime = LocalDateTime.now();
        List<Integer> userItemIds = userItems.stream().map(Item::getId).collect(Collectors.toList());
        Map<Integer, List<Booking>> lastBookings = bookingRepository
                .findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(userItemIds, user.getId(), nowDateTime, NEGATIVE_BOOKING_STATUSES)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Integer, List<Booking>> nextBookings = bookingRepository
                .findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(userItemIds, user.getId(), nowDateTime, NEGATIVE_BOOKING_STATUSES)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        Map<Integer, List<Comment>> comments = commentRepository
                .findAllByItemIdInOrderByIdAsc(userItemIds)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        List<ItemBookingInfo> itemBookingsInfo =
                userItems.stream().map(it ->
                        new ItemBookingInfo(
                                it,
                                lastBookings.getOrDefault(it.getId(), new ArrayList<>()).stream().findFirst().orElse(null),
                                nextBookings.getOrDefault(it.getId(), new ArrayList<>()).stream().findFirst().orElse(null),
                                comments.getOrDefault(it.getId(), new ArrayList<>())
                        )
                ).collect(Collectors.toList());
        return itemBookingsInfo;
    }

    @Override
    public List<Item> searchAvailableItems(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            if (size <= 0) {
                throw new IllegalArgumentException("Размер должен быть больше нуля!");
            }
            return itemRepository.searchAvailable(text, PageRequest.of(from / size, size));
        }
    }

    @Override
    @Transactional
    public Comment addComment(Comment comment, int itemId, int userId) throws ForbiddenException {
        LocalDateTime nowDateTime = LocalDateTime.now();
        Booking booking = bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBefore(itemId, userId, nowDateTime).orElseThrow(() -> new IllegalArgumentException("Бронирование для такого пользователя не найдено!"));
        comment.setAuthor(booking.getBooker());
        comment.setItem(booking.getItem());
        comment.setCreated(nowDateTime);
        return commentRepository.save(comment);
    }
}
