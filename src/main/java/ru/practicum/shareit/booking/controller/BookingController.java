package ru.practicum.shareit.booking.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.UnsupportedStatusException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @RequestBody @Valid BookingDto bookingDto) {
        Booking addedBooking = bookingService.addBooking(
                BookingMapper.toBooking(bookingDto), bookingDto.getItemId(), ownerId);
        return BookingMapper.toBookingDto(addedBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @PathVariable("bookingId") Integer bookingId,
                                     @RequestParam("approved") Boolean isApproved) {
        Booking booking = bookingService.approveBooking(bookingId, ownerId, isApproved);
        return BookingMapper.toBookingDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @PathVariable("bookingId") Integer bookingId) {
        Booking booking = bookingService.getBooking(bookingId, ownerId);
        return BookingMapper.toBookingDto(booking);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsOfUserByState(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException();
        }
        List<Booking> bookings = bookingService.getAllBookingsOfUserByState(ownerId, bookingState, from, size);
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsOfUserItems(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException();
        }
        List<Booking> bookings = bookingService.getAllBookingsOfUserItems(ownerId, bookingState, from, size);
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
