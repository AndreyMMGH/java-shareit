package ru.practicum.shareit.request.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public static ItemReqResponseDto toItemResponseDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemReqResponseDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor().getId(),
                itemRequest.getCreated(),
                items
        );
    }

    public static ItemRequest toItemRequest(ItemReqRequestDto itemReqRequestDto, User user) {
        return new ItemRequest(
                null,
                itemReqRequestDto.getDescription(),
                user,
                LocalDateTime.now()
        );
    }
}
