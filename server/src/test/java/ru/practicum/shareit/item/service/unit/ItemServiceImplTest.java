package ru.practicum.shareit.item.service.unit;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingInfo;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemServiceImplTest {
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);

    private final ItemService itemService = new ItemServiceImpl(userRepository, itemRepository, bookingRepository, commentRepository, itemRequestRepository);

    private final EasyRandom generator = new EasyRandom();

    private User user;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
    }

    @Test
    void addItem() {
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(generator.nextObject(ItemRequest.class)));
        Item item = generator.nextObject(Item.class);
        item.setRequestId(generator.nextInt());
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        Item addedItem = itemService.addItem(item, user.getId());
        assertEquals(item, addedItem);
    }

    @Test
    void updateItem() {
        Item item = generator.nextObject(Item.class);
        item.setOwner(user);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        Item updatedItem = itemService.updateItem(item, user.getId());
        assertEquals(item, updatedItem);
    }

    @Test
    void getItemByIdAndUserIdTest() {
        Item item = generator.nextObject(Item.class);
        item.setOwner(user);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        List<Comment> comments = generator.objects(Comment.class, 10).collect(Collectors.toList());
        when(commentRepository.findAllByItemId(anyInt())).thenReturn(comments);
        Booking lastBooking = generator.nextObject(Booking.class);
        when(bookingRepository.findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(anyList(), anyInt(), any(LocalDateTime.class), anyList())).thenReturn(List.of(lastBooking));
        Booking nextBooking = generator.nextObject(Booking.class);
        when(bookingRepository.findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(anyList(), anyInt(), any(LocalDateTime.class), anyList())).thenReturn(List.of(nextBooking));
        ItemBookingInfo itemBookingInfo = itemService.getItemByIdAndUserId(item.getId(), user.getId());
        assertEquals(item, itemBookingInfo.getItem());
        assertEquals(lastBooking, itemBookingInfo.getLastBooking());
        assertEquals(nextBooking, itemBookingInfo.getNextBooking());
        assertEquals(comments, itemBookingInfo.getComments());
    }

    @Test
    void getOwnerItemsTest() {
        int itemId = generator.nextInt();
        List<Item> items = generator.objects(Item.class, 20).collect(Collectors.toList());
        items.get(0).setId(itemId);
        when(itemRepository.findByOwnerId(anyInt(), any(Pageable.class))).thenReturn(items);
        List<Booking> lastBookings = generator.objects(Booking.class, 3).collect(Collectors.toList());
        lastBookings.forEach(b -> b.setItem(items.get(0)));
        when(bookingRepository.findLastByItemIdsAndItemOwnerIdAndStartIsBeforeAndStatusNotIn(anyList(), anyInt(), any(LocalDateTime.class), anyList())).thenReturn(lastBookings);
        List<Booking> nextBookings = generator.objects(Booking.class, 3).collect(Collectors.toList());
        nextBookings.forEach(b -> b.setItem(items.get(0)));
        when(bookingRepository.findNextByItemIdsAndItemOwnerIdAndStartIsAfterAndStatusNotIn(anyList(), anyInt(), any(LocalDateTime.class), anyList())).thenReturn(nextBookings);
        List<Comment> comments = generator.objects(Comment.class, 20).collect(Collectors.toList());
        comments.forEach(c -> c.setItem(items.get(0)));
        when(commentRepository.findAllByItemIdInOrderByIdAsc(anyList())).thenReturn(comments);
        List<ItemBookingInfo> itemBookingInfos = itemService.getOwnerItems(user.getId(), 0, 20);
        ItemBookingInfo itemInfo = itemBookingInfos.stream().filter(i -> i.getItem().getId() == itemId).findFirst().get();
        assertEquals(comments, itemInfo.getComments());
        assertEquals(lastBookings.stream().findFirst().get(), itemInfo.getLastBooking());
        assertEquals(nextBookings.stream().findFirst().get(), itemInfo.getNextBooking());
    }

    @Test
    void shouldReturnEmptyListIfItemsNotFoundByOwner() {
        when(itemRepository.findByOwnerId(anyInt(), any(Pageable.class))).thenReturn(Collections.emptyList());
        assertEquals(0, itemService.getOwnerItems(user.getId(), 0, 10).size());
    }

    @Test
    void searchAvailableItemsTest() {
        List<Item> items = generator.objects(Item.class, 10).collect(Collectors.toList());
        when(itemRepository.searchAvailable(anyString(), any(Pageable.class))).thenReturn(items);
        List<Item> foundItems = itemService.searchAvailableItems(generator.nextObject(String.class), 0, 10);
        assertEquals(items, foundItems);
    }


    @Test
    void addCommentTest() {
        Booking booking = generator.nextObject(Booking.class);
        Comment comment = generator.nextObject(Comment.class);
        when(bookingRepository.findFirstByItemIdAndBookerIdAndEndIsBefore(anyInt(), anyInt(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        Comment addedComment = itemService.addComment(comment, booking.getItem().getId(), booking.getBooker().getId());
        assertEquals(comment, addedComment);
    }

    @Test
    void searchAvailableItemsShouldReturnEmptyListTest() {
        List<Item> foundItems = itemService.searchAvailableItems("unknown ", 0, 5);
        assertTrue(foundItems.isEmpty());
    }
}
