package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    public Item addItem(Item item);

    public Item updateItem(Item item);

    public Item getItemById(Integer id);

    public List<Item> getUserItems(Integer userId);

    public List<Item> getAllItems();
}
