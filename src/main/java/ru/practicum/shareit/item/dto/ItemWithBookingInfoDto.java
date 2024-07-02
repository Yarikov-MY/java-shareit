package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithBookingInfoDto {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
    private Integer requestId;
    private BookingDto nextBooking;
    private BookingDto lastBooking;
    private List<CommentDto> comments;
}
