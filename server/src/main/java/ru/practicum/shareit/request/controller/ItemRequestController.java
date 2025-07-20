package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemReqResponseDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemReqRequestDto itemReqRequestDto) {
        log.info("POST /requests");
        return itemRequestService.createItemRequest(userId, itemReqRequestDto);
    }

    @GetMapping
    public List<ItemReqResponseDto> findListOfYourQueriesWithAnswers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests");
        return itemRequestService.findListOfYourQueriesWithAnswers(userId);
    }

    @GetMapping("/all")
    public List<ItemReqResponseDto> findListOfRequestsOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                 @RequestParam(defaultValue = "0") int from,
                                                                 @RequestParam(defaultValue = "10") int size) {
        log.info("GET /requests/all");
        return itemRequestService.findListOfRequestsOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemReqResponseDto findYourQueryWithAnswers(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("GET /requests/{}", requestId);
        return itemRequestService.findYourQueryWithAnswers(userId, requestId);
    }
}
