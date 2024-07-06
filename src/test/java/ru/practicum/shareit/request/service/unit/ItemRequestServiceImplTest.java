package ru.practicum.shareit.request.service.unit;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemRequestServiceImplTest {
    private final ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private final ItemRequestService itemRequestService = new ItemRequestServiceImpl(
            userRepository, itemRequestRepository
    );

    private final EasyRandom generator = new EasyRandom();

    private User user;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
    }

    @Test
    void addRequestTest() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        ItemRequest addedItemRequest = itemRequestService.addRequest(itemRequest, user.getId());
        assertEquals(itemRequest, addedItemRequest);
    }

    @Test
    void getRequestTest() {
        ItemRequest itemRequest = generator.nextObject(ItemRequest.class);
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));
        ItemRequest foundItemRequest = itemRequestService.getRequest(itemRequest.getId(), user.getId());
        assertEquals(itemRequest, foundItemRequest);
    }

    @Test
    void getAllRequestsByRequestorIdTest() {
        List<ItemRequest> itemRequests = generator.objects(ItemRequest.class, 20).collect(Collectors.toList());
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(anyInt())).thenReturn(itemRequests);
        List<ItemRequest> foundItemRequests = itemRequestService.getAllRequestsByRequestorId(user.getId());
        assertEquals(itemRequests, foundItemRequests);
    }

    @Test
    void getAllRequestsTest() {
        Page<ItemRequest> itemRequests = new PageImpl<>(generator.objects(ItemRequest.class, 20).collect(Collectors.toList()));
        when(itemRequestRepository.findByRequestorIdNot(anyInt(), any(Pageable.class))).thenReturn(itemRequests);
        List<ItemRequest> foundItemRequest = itemRequestService.getAllRequests(user.getId(), 0, 20);
        assertEquals(itemRequests.getContent(), foundItemRequest);
    }
}
