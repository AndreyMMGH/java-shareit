package ru.practicum.shareit.booking.dto.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SimplifiedBookingDto {
    private Long id;
    private Long bookerId;
}
