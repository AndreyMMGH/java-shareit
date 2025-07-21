package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemReqResponseDto createItemRequest(Long userId, ItemReqRequestDto itemReqRequestDto) {
        User user = validateUser(userId);

        return ItemRequestMapper.toItemResponseDto(itemRequestRepository
                .save(ItemRequestMapper.toItemRequest(itemReqRequestDto, user)), List.of());
    }

    @Override
    public List<ItemReqResponseDto> getRequests(Long userId) {
        validateUser(userId);

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        return composeResponseForItemResponseDto(requests);
    }

    @Override
    public List<ItemReqResponseDto> getRequestsOtherUsers(Long userId, int from, int size) {
        validateUser(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNot(userId, pageable).getContent();

        return composeResponseForItemResponseDto(requests);
    }

    @Override
    public ItemReqResponseDto getRequestWithAnswers(Long userId, Long requestId) {
        validateUser(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи с данным id: " + requestId + " не найден"));

        List<Item> items = itemRepository.findAllByItemRequestIdIn(List.of(itemRequest.getId()));

        return ItemRequestMapper.toItemResponseDto(itemRequest,
                items
                        .stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList()));
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id: " + userId + " не найден"));
    }

    private List<ItemReqResponseDto> composeResponseForItemResponseDto(List<ItemRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        if (requestIds.isEmpty()) return List.of();

        List<Item> items = itemRepository.findAllByItemRequestIdIn(requestIds);

        Map<Long, List<ItemDto>> itemsByRequest = items
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(req -> ItemRequestMapper.toItemResponseDto(
                        req,
                        itemsByRequest.getOrDefault(req.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }
}
