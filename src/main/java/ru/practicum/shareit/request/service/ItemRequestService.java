package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addRequest(ItemRequest itemRequest, Integer requestorId) throws UserNotFoundException;

    ItemRequest getRequest(Integer requestId, Integer requestorId) throws UserNotFoundException, ItemRequestNotFoundException;

    List<ItemRequest> getAllRequests(Integer requestorId, Integer from, Integer size);

    List<ItemRequest> getAllRequestsByRequestorId(Integer requestorId);
}
