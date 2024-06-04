package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Item item, Integer ownerId);

    Item updateItem(Item item, Integer ownerId);

    Item getItemById(Integer itemId);

    List<Item> getUserItems(Integer userId);

    List<Item> searchItems(String text, Integer notForUserId);

}
