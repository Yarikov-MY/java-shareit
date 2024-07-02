package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequestServiceImpl(UserRepository userRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemRequest addRequest(ItemRequest itemRequest, Integer requestorId) throws UserNotFoundException {
        User requestor = userRepository.findById(requestorId).orElseThrow(() -> new UserNotFoundException(requestorId));
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequest getRequest(Integer requestId, Integer requestorId) throws UserNotFoundException, ItemRequestNotFoundException {
        userRepository.findById(requestorId).orElseThrow(() -> new UserNotFoundException(requestorId));
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new ItemRequestNotFoundException(requestId));
    }

    @Override
    public List<ItemRequest> getAllRequests(Integer requestorId, Integer from, Integer size) {
        return itemRequestRepository.findByRequestorIdNot(requestorId,
                PageRequest.of(from, size, Sort.by("created").descending())).getContent();
    }

    @Override
    public List<ItemRequest> getAllRequestsByRequestorId(Integer requestorId) {
        userRepository.findById(requestorId).orElseThrow(() -> new UserNotFoundException(requestorId));
        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);
    }
}
