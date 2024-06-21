package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();

    private final AtomicInteger id = new AtomicInteger(0);

    @Override
    public Item addItem(Item item) {
        item.setId(id.incrementAndGet());
        return items.put(item.getId(), item);
    }

    @Override
    public Item updateItem(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
            return item;
        } else {
            throw new NotFoundException("Предмет с id=" + item.getId() + " не найден!");
        }
    }

    @Override
    public Item getItemById(Integer id) {
        return items.get(id);
    }

    @Override
    public List<Item> getUserItems(Integer userId) {
        return items.values().stream().filter(item -> Objects.equals(item.getOwner().getId(), userId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }
}
