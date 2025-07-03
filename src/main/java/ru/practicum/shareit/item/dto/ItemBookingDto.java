package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.SimplifiedBookingDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private SimplifiedBookingDto lastBooking;
    private SimplifiedBookingDto nextBooking;
    private List<CommentResponseDto> comments;
}
