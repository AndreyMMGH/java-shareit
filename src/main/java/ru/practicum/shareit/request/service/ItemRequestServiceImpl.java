package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;

public abstract class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public List<ItemResponseDto> findListOfRequestsOtherUsers(Long userId, int from, int size) {
        return List.of();
    }

    @Override
    public List<ItemResponseDto> findListOfYourQueriesWithAnswers(Long userId) {
        return List.of();
    }

    @Override
    public ItemResponseDto findYourQueryWithAnswers(Long userId, Long requestId) {
        return null;
    }

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        return null;
    }
}
