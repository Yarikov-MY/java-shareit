package ru.practicum.shareit.request.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto addRequest(
            @RequestHeader("X-Sharer-User-Id") Integer requestorId,
            @RequestBody @Valid ItemRequestDto itemRequestDto
    ) {
        ItemRequest addedItemRequest = itemRequestService.addRequest(ItemRequestMapper.toItemRequest(itemRequestDto), requestorId);
        return ItemRequestMapper.toItemRequestDto(addedItemRequest);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequest(
            @RequestHeader("X-Sharer-User-Id") Integer requestorId, @PathVariable Integer requestId
    ) {
        return ItemRequestMapper.toItemRequestDto(itemRequestService.getRequest(requestId, requestorId));
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByRequestorId(
            @RequestHeader("X-Sharer-User-Id") Integer requestorId
    ) {
        return itemRequestService.getAllRequestsByRequestorId(requestorId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Integer requestorId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return itemRequestService.getAllRequests(requestorId, from, size)
                .stream().map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
}
