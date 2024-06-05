package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        if (item != null) {
            return new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getRequest() != null ? item.getRequest().getId() : null
            );
        } else {
            throw new NullPointerException("Передан пустой объект item!");
        }
    }

    public static Item toItem(ItemDto itemDto) {
        if (itemDto != null) {
            return new Item(null, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), null, null);
        } else {
            throw new NullPointerException("Передан пустой объект itemDto!");
        }
    }

    public static Item toItem(ItemDto itemDto, Integer itemId) {
        if (itemDto != null) {
            return new Item(itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), null, null);
        } else {
            throw new NullPointerException("Передан пустой объект itemDto!");
        }
    }

}
