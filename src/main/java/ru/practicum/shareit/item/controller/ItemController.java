package ru.practicum.shareit.item.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(value = "X-Sharer-User-Id") Integer userId, @Valid @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.addItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(value = "X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto, @PathVariable Integer itemId) {
        Item item = ItemMapper.toItem(itemDto, itemId);
        return ItemMapper.toItemDto(itemService.updateItem(item, userId));
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingInfoDto getItem(@RequestHeader(value = "X-Sharer-User-Id") Integer userId, @PathVariable Integer itemId) {
        return ItemMapper.toItemWithBookingInfoDto(itemService.getItemByIdAndUserId(itemId, userId));
    }

    @GetMapping
    public List<ItemWithBookingInfoDto> getUserItems(@RequestHeader(value = "X-Sharer-User-Id") Integer userId) {
        return itemService.getOwnerItems(userId).stream().map(ItemMapper::toItemWithBookingInfoDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = false, defaultValue = "") String text) {
        return itemService.searchAvailableItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestBody @Valid CommentDto commentDto,
            @PathVariable Integer itemId
    ) {
        Comment addedComment = itemService.addComment(CommentMapper.toComment(commentDto), itemId, ownerId);
        return CommentMapper.toCommentDto(addedComment);
    }
}
