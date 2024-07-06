package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.InvalidBookingStatusException;
import ru.practicum.shareit.booking.exception.UnsupportedStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.exception.ForbiddenException;
import ru.practicum.shareit.user.exception.DuplicateEmailException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom generator = new EasyRandom();
    @MockBean
    private BookingService bookingService;

    private Booking booking;

    @BeforeEach
    void beforeEach() {
        booking = generator.nextObject(Booking.class);
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    void addBookingTest() throws Exception {
        when(bookingService.addBooking(any(Booking.class), anyInt(), anyInt())).thenReturn(booking);
        BookingDto reqBookingDto = new BookingDto(null, booking.getStart(), booking.getEnd(), null, booking.getItem().getId(), null, booking.getBooker().getId(), null);
        MvcResult result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(reqBookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookingDto resBookingDto = objectMapper.readValue(result.getResponse().getContentAsString(), BookingDto.class);
        assertEquals(booking.getId(), resBookingDto.getId());
        assertEquals(booking.getBooker().getId(), resBookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), resBookingDto.getItem().getId());

    }

    @Test
    void getBadRequestAfterTryToAddBookingWithPastEndTimeTest() throws Exception {
        BookingDto reqBookingDto = new BookingDto(null, booking.getStart(), LocalDateTime.now().minusDays(1), null, booking.getItem().getId(), null, booking.getBooker().getId(), null);
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(reqBookingDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBookingTest() throws Exception {
        booking.setStatus(Status.APPROVED);
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(booking);
        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(booking.getStatus().name()));
    }

    @Test
    void shouldReturnBadRequestAfterThrowInvalidBookingStatusExceptionWhenApproveBookingTest() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new InvalidBookingStatusException(generator.nextObject(String.class)));
        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestAfterThrowHttpMessageNotReadableExceptionWhenApproveBookingTest() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new HttpMessageNotReadableException(generator.nextObject(String.class)));
        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500AfterThrowDuplicateEmailExceptionWhenApproveBookingTest() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new DuplicateEmailException(generator.nextObject(String.class)));
        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldReturn500AfterThrowUnsupportedStatusExceptionWhenApproveBookingTest() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new UnsupportedStatusException());
        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldReturn500AfterThrowForbiddenExceptionWhenApproveBookingTest() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new ForbiddenException(""));
        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn500AfterThrowThrowableWhenApproveBookingTest() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new RuntimeException(""));
        mockMvc.perform(patch("/bookings/" + booking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getBookingTest() throws Exception {
        when(bookingService.getBooking(anyInt(), anyInt())).thenReturn(booking);
        MvcResult result = mockMvc.perform(get("/bookings/" + booking.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn();
        BookingDto resBookingDto = objectMapper.readValue(result.getResponse().getContentAsString(), BookingDto.class);
        assertEquals(booking.getId(), resBookingDto.getId());
        assertEquals(booking.getBooker().getId(), resBookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), resBookingDto.getItem().getId());
    }

    @Test
    void shouldReturn404AfterThrowBookingNotFoundExceptionWhenGetBookingTest() throws Exception {
        when(bookingService.getBooking(anyInt(), anyInt())).thenThrow(new BookingNotFoundException());
        mockMvc.perform(get("/bookings/" + booking.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }


    @Test
    void getAllBookingsOfUserByStateTest() throws Exception {
        when(bookingService.getAllBookingsOfUserByState(anyInt(), any(State.class), anyInt(), anyInt())).thenReturn(List.of(booking));
        MvcResult result = mockMvc.perform(get("/bookings")
                        .param("state", State.ALL.name())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDto> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(booking.getId(), response.get(0).getId());
        assertEquals(booking.getBooker().getId(), response.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), response.get(0).getItem().getId());
    }

    @Test
    void getAllBookingsOfUserItemsTest() throws Exception {
        when(bookingService.getAllBookingsOfUserItems(anyInt(), any(State.class), anyInt(), anyInt())).thenReturn(List.of(booking));
        MvcResult result = mockMvc.perform(get("/bookings/owner")
                        .param("state", State.ALL.name())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn();
        List<BookingDto> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(booking.getId(), response.get(0).getId());
        assertEquals(booking.getBooker().getId(), response.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), response.get(0).getItem().getId());
    }
}
