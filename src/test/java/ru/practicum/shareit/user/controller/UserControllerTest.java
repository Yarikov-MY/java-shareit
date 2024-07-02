package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    private final EasyRandom generator = new EasyRandom();
    private UserDto requestUserDTO;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = generator.nextObject(User.class);
        user.setEmail("email@email.ru");
        requestUserDTO = new UserDto(null, user.getName(), user.getEmail());
    }

    @Test
    void addUserTest() throws Exception {
        when(userService.addUser(any(User.class))).thenReturn(user);
        MvcResult result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(requestUserDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        UserDto responseUserDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserDto.class
        );
        assertEquals(user.getId(), responseUserDto.getId());
        assertEquals(user.getName(), responseUserDto.getName());
        assertEquals(user.getEmail(), responseUserDto.getEmail());
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(any(User.class))).thenReturn(user);
        MvcResult result = mockMvc.perform(patch("/users/" + user.getId())
                        .content(objectMapper.writeValueAsString(requestUserDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserDto responseUserDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserDto.class
        );
        assertEquals(user.getId(), responseUserDto.getId());
        assertEquals(user.getName(), responseUserDto.getName());
        assertEquals(user.getEmail(), responseUserDto.getEmail());
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(anyInt())).thenReturn(user);
        MvcResult result = mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andReturn();
        UserDto responseUserDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UserDto.class
        );
        assertEquals(user.getId(), responseUserDto.getId());
        assertEquals(user.getName(), responseUserDto.getName());
        assertEquals(user.getEmail(), responseUserDto.getEmail());
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(user));
        MvcResult result = mockMvc.perform(get("/users")).andExpect(status().isOk()).andReturn();
        List<UserDto> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(user.getId(), response.get(0).getId());
        assertEquals(user.getName(), response.get(0).getName());
        assertEquals(user.getEmail(), response.get(0).getEmail());
    }

    @Test
    void deleteUserTest() throws Exception {
        doNothing().when(userService).deleteUser(anyInt());
        mockMvc.perform(delete("/users/" + user.getId())).andExpect(status().isOk()).andReturn();
    }
}
