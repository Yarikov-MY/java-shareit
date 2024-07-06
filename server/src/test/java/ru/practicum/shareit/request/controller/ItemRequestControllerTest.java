package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();
    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        itemRequest = generator.nextObject(ItemRequest.class);
    }

    @Test
    void addRequestTest() throws Exception {
        when(itemRequestService.addRequest(any(ItemRequest.class), anyInt())).thenReturn(itemRequest);
        ItemRequestDto itemRequestDto = generator.nextObject(ItemRequestDto.class);
        MvcResult result = mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ItemRequestDto itemResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ItemRequestDto.class
        );
        assertEquals(itemRequest.getId(), itemResponseDto.getId());
        assertEquals(itemRequest.getDescription(), itemResponseDto.getDescription());

    }

    @Test
    void getRequestTest() throws Exception {
        when(itemRequestService.getRequest(anyInt(), anyInt())).thenReturn(itemRequest);
        MvcResult result = mockMvc.perform(get("/requests/" + itemRequest.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn();
        ItemRequestDto responseItemRequestDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ItemRequestDto.class
        );
        assertEquals(itemRequest.getId(), responseItemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), responseItemRequestDto.getDescription());
        assertEquals(
                itemRequest.getItems().stream().map(Item::getId).sorted(Comparator.comparingInt(i -> i)).collect(Collectors.toList()),
                responseItemRequestDto.getItems().stream().map(ItemDto::getId).sorted(Comparator.comparingInt(i -> i)).collect(Collectors.toList())
        );
    }

    @Test
    void getAllRequestsByRequestorIdTest() throws Exception {
        when(itemRequestService.getAllRequestsByRequestorId(anyInt()))
                .thenReturn(List.of(itemRequest));

        MvcResult result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn();
        ItemRequestDto response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ItemRequestDto>>() {
        }).get(0);
        assertEquals(itemRequest.getId(), response.getId());
        assertEquals(itemRequest.getDescription(), response.getDescription());
        assertEquals(
                itemRequest.getItems().stream().map(Item::getId).sorted(Comparator.comparingInt(i -> i)).collect(Collectors.toList()),
                response.getItems().stream().map(ItemDto::getId).sorted(Comparator.comparingInt(i -> i)).collect(Collectors.toList()));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        when(itemRequestService.getAllRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemRequest));
        MvcResult result = mockMvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn();
        ItemRequestDto response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<ItemRequestDto>>() {
        }).get(0);
        assertEquals(itemRequest.getId(), response.getId());
        assertEquals(itemRequest.getDescription(), response.getDescription());
        assertEquals(
                itemRequest.getItems().stream().map(Item::getId).sorted(Comparator.comparingInt(i -> i)).collect(Collectors.toList()),
                response.getItems().stream().map(ItemDto::getId).sorted(Comparator.comparingInt(i -> i)).collect(Collectors.toList()));
    }
}
