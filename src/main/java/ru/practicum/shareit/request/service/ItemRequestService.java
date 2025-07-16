package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemResponseDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemResponseDto> findListOfYourQueriesWithAnswers(Long userId);

    List<ItemResponseDto> findListOfRequestsOtherUsers(Long userId, int from, int size);

    ItemResponseDto findYourQueryWithAnswers(Long userId, Long requestId);
}
