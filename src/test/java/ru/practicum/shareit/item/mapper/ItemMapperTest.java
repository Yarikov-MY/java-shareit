package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemMapperTest {
    @Test
    void toItemNullDto() {
        assertNull(ItemMapper.toItem(null));
    }

    @Test
    void toItem2paramsNullDto() {
        assertNull(ItemMapper.toItem(null, 1));
    }

    @Test
    void toItemDtoNullModel() {
        assertNull(ItemMapper.toItemDto(null));
    }

    @Test
    void toItemWithBookingInfoDto() {
        assertNull(ItemMapper.toItemWithBookingInfoDto(null));
    }
}
