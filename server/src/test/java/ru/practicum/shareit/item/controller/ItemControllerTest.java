package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemBookingInfo;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();
    @MockBean
    private ItemService itemService;


    private Item item;
    private ItemBookingInfo itemBookingInfo;

    @BeforeEach
    void beforeEach() {
        item = generator.nextObject(Item.class);
        itemBookingInfo = generator.nextObject(ItemBookingInfo.class);
        itemBookingInfo.setItem(item);
    }

    @Test
    void addItemTest() throws Exception {
        when(itemService.addItem(any(Item.class), anyInt())).thenReturn(item);
        ItemDto itemDto = generator.nextObject(ItemDto.class);
        itemDto.setAvailable(true);
        MvcResult result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ItemDto responseItemDTO = objectMapper.readValue(
                result.getResponse().getContentAsString(), ItemDto.class
        );
        assertEquals(item.getId(), responseItemDTO.getId());
        assertEquals(item.getName(), responseItemDTO.getName());
        assertEquals(item.getDescription(), responseItemDTO.getDescription());
        assertEquals(item.getAvailable(), responseItemDTO.getAvailable());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(Item.class), anyInt())).thenReturn(item);
        ItemDto itemDto = generator.nextObject(ItemDto.class);
        itemDto.setAvailable(true);
        MvcResult result = mockMvc.perform(patch("/items/" + item.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ItemDto responseItemDTO = objectMapper.readValue(result.getResponse().getContentAsString(), ItemDto.class);
        assertEquals(item.getId(), responseItemDTO.getId());
        assertEquals(item.getName(), responseItemDTO.getName());
        assertEquals(item.getDescription(), responseItemDTO.getDescription());
        assertEquals(item.getAvailable(), responseItemDTO.getAvailable());
    }

    @Test
    void getItemByIdAndUserIdTest() throws Exception {
        when(itemService.getItemByIdAndUserId(anyInt(), anyInt())).thenReturn(itemBookingInfo);
        MvcResult result = mockMvc.perform(get("/items/" + item.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn();
        ItemWithBookingInfoDto responseItemDTO = objectMapper.readValue(result.getResponse().getContentAsString(), ItemWithBookingInfoDto.class);
        assertEquals(itemBookingInfo.getComments().size(), responseItemDTO.getComments().size());
    }

    @Test
    void getAllItemsByOwnerIdTest() throws Exception {
        when(itemService.getOwnerItems(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemBookingInfo));
        MvcResult result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn();
        List<ItemWithBookingInfoDto> responseItemDTO = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(itemBookingInfo.getComments().size(), responseItemDTO.get(0).getComments().size());
    }

    @Test
    void searchAvailableItemsTest() throws Exception {
        when(itemService.searchAvailableItems(anyString(), anyInt(), anyInt())).thenReturn(List.of(item));
        MvcResult result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", generator.nextObject(String.class)))
                .andExpect(status().isOk())
                .andReturn();
        List<ItemDto> responseItemDTO = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(item.getId(), responseItemDTO.get(0).getId());
        assertEquals(item.getName(), responseItemDTO.get(0).getName());
        assertEquals(item.getDescription(), responseItemDTO.get(0).getDescription());
        assertEquals(item.getAvailable(), responseItemDTO.get(0).getAvailable());
    }

    @Test
    void addCommentTest() throws Exception {
        Comment comment = generator.nextObject(Comment.class);
        when(itemService.addComment(any(Comment.class), anyInt(), anyInt())).thenReturn(comment);
        CommentDto commentDto = generator.nextObject(CommentDto.class);
        commentDto.setText(comment.getText());
        MvcResult result = mockMvc.perform(post("/items/" + item.getId() + "/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        CommentDto responseCommentDTO = objectMapper.readValue(result.getResponse().getContentAsString(), CommentDto.class);
        assertEquals(comment.getId(), responseCommentDTO.getId());
        assertEquals(comment.getText(), responseCommentDTO.getText());
        assertEquals(comment.getAuthor().getName(), responseCommentDTO.getAuthorName());
    }
}
