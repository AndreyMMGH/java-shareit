package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST /requests");
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findListOfYourQueriesWithAnswers(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests");
        return itemRequestClient.findListOfYourQueriesWithAnswers(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findListOfRequestsOtherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                               @RequestParam(defaultValue = "0") int from,
                                                               @RequestParam(defaultValue = "10") int size) {
        log.info("GET /requests/all");
        return itemRequestClient.findListOfRequestsOtherUsers(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findYourQueryWithAnswers(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("GET /requests/{}", requestId);
        return itemRequestClient.findYourQueryWithAnswers(userId, requestId);
    }
}
