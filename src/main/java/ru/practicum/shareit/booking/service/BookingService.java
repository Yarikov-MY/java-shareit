package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.InvalidBookingStatusException;
import ru.practicum.shareit.booking.exception.ItemNotAvailableException;
import ru.practicum.shareit.booking.exception.UserNotOwnerOrCreator;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

public interface BookingService {
    Booking addBooking(Booking booking, Integer itemId, Integer bookerId) throws ItemNotFoundException, UserNotFoundException, ItemNotAvailableException;

    Booking approveBooking(Integer bookingId, Integer ownerId, Boolean isApproved) throws BookingNotFoundException, UserNotOwnerOrCreator, InvalidBookingStatusException;

    Booking getBooking(Integer bookingId, Integer userId) throws BookingNotFoundException, UserNotOwnerOrCreator;

    List<Booking> getAllBookingsOfUserByState(Integer bookerId, State state, Integer from, Integer size) throws BookingNotFoundException;

    List<Booking> getAllBookingsOfUserItems(Integer ownerId, State state, Integer from, Integer size) throws BookingNotFoundException;
}
