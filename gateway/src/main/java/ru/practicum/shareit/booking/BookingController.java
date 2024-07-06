package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive Integer ownerId,
            @RequestBody @Valid BookItemRequestDto bookingDTO
    ) {
        return bookingClient.addBooking(ownerId, bookingDTO);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive Integer ownerId,
            @PathVariable("bookingId") Integer bookingId,
            @RequestParam("approved") Boolean isApproved
    ) {
        return bookingClient.approveBooking(ownerId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive Integer ownerId,
            @PathVariable("bookingId") Integer bookingId
    ) {
        return bookingClient.getBooking(ownerId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsOfUserByState(
            @RequestHeader("X-Sharer-User-Id")
            @Positive Integer ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        State bookingState = State.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));

        return bookingClient.getAllBookingsOfUserByState(ownerId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOfUserItems(
            @RequestHeader("X-Sharer-User-Id")
            @Positive Integer ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        State bookingState = State.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));

        return bookingClient.getAllBookingsOfUserItems(ownerId, bookingState, from, size);
    }
}
