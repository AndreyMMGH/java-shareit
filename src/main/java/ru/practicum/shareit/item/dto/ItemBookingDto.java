package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.mapper.SimplifiedBookingDto;

@AllArgsConstructor
@Data
public class ItemBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private SimplifiedBookingDto lastBooking;
    private SimplifiedBookingDto nextBooking;
}
