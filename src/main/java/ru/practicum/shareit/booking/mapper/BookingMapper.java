package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        } else {
            return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), null, null, bookingDto.getStatus());
        }
    }

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), ItemMapper.toItemDto(booking.getItem()), null, UserMapper.toUserDto(booking.getBooker()), booking.getBooker().getId(), booking.getStatus());
        }
    }


}
