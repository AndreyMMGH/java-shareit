package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemReqResponseDto createItemRequest(Long userId, ItemReqRequestDto itemReqRequestDto);

    List<ItemReqResponseDto> getRequests(Long userId);

    List<ItemReqResponseDto> getRequestsOtherUsers(Long userId, int from, int size);

    ItemReqResponseDto getRequestWithAnswers(Long userId, Long requestId);
}
