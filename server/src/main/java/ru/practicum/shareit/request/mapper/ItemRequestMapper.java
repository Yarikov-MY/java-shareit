package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        } else {
            ItemRequestDto itemRequestDto = new ItemRequestDto(
                    itemRequest.getId(),
                    itemRequest.getDescription(),
                    null,
                    itemRequest.getCreated(),
                    null
            );
            List<ItemDto> items = Collections.emptyList();
            if (itemRequest.getItems() != null) {
                items = itemRequest.getItems().stream().map(it -> {
                            Integer itemRequestId = null;
                            if (it.getRequest() != null) {
                                itemRequestId = it.getRequest().getId();
                            }
                            return new ItemDto(it.getId(), it.getName(), it.getDescription(), it.getAvailable(), itemRequestId);
                        }
                ).collect(Collectors.toList());
            }
            itemRequestDto.setItems(items);
            return itemRequestDto;
        }
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            return null;
        } else {
            return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), null, itemRequestDto.getCreated(), null);
        }
    }
}
