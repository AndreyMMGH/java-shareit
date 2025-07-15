package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST /requests");
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemResponseDto> findListOfYourQueriesWithAnswers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests");
        return itemRequestService.findListOfYourQueriesWithAnswers(userId);
    }

    @GetMapping("/all")
    public List<ItemResponseDto> findListOfRequestsOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(defaultValue = "0") int from,
                                                              @RequestParam(defaultValue = "10") int size) {
        log.info("GET /requests/all");
        return itemRequestService.findListOfRequestsOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemResponseDto findYourQueryWithAnswers(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("GET /requests/{}", requestId);
        return itemRequestService.findYourQueryWithAnswers(userId, requestId);
    }
}
