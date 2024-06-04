package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Item addItem(Item item, Integer ownerId) {
        User user = userRepository.getUserById(ownerId);
        if (user != null) {
            item.setOwner(user);
            itemRepository.addItem(item);
            return item;
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    @Override
    public Item updateItem(Item item, Integer ownerId) {
        Item updatedItem = itemRepository.getItemById(item.getId());
        if (updatedItem != null && Objects.equals(updatedItem.getOwner().getId(), ownerId)) {
            if (item.getName() != null) {
                updatedItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                updatedItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                updatedItem.setAvailable(item.getAvailable());
            }
            itemRepository.updateItem(updatedItem);
            return updatedItem;
        } else {
            throw new ForbiddenException("Попытка изменить чужую вещь!");
        }
    }

    @Override
    public Item getItemById(Integer itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getUserItems(Integer userId) {
        return itemRepository.getUserItems(userId);
    }

    @Override
    public List<Item> searchItems(String text, Integer notForUserId) {
        String lowerCaseText = text.toLowerCase();
        if (lowerCaseText.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.getAllItems().stream().filter(item ->
                    item.getAvailable()
                            && (item.getName().toLowerCase().contains(lowerCaseText)
                            || item.getDescription().toLowerCase().contains(lowerCaseText))
            ).collect(Collectors.toList());
        }
    }
}
