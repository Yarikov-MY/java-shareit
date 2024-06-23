package ru.practicum.shareit.item.service;

import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingInfo;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface ItemService {
    Item addItem(Item item, Integer ownerId) throws UserNotFoundException;

    Item updateItem(Item item, Integer ownerId) throws UserNotFoundException, ItemNotFoundException, ForbiddenException;

    ItemBookingInfo getItemByIdAndUserId(Integer itemId, Integer userId) throws UserNotFoundException, ItemNotFoundException;

    List<ItemBookingInfo> getOwnerItems(Integer ownerId) throws UserNotFoundException;

    List<Item> searchAvailableItems(String text);

    Comment addComment(Comment comment, int itemId, int userId) throws ForbiddenException;

}
