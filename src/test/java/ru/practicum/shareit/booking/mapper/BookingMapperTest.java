package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTest {
    @Test
    void toBookingNullDto() {
        assertNull(BookingMapper.toBookingDto(null));
    }

    @Test
    void toBookingDtoNullModel() {
        assertNull(BookingMapper.toBooking(null));
    }
}
