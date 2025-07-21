package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;


@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("POST /items");
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId, @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/{}", itemId);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
        log.info("GET /items/{}", itemId);
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items");
        return itemClient.findUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(name = "text") String text) {
        log.info("GET /items/search?text={}.", text);
        return itemClient.searchItem(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable("itemId") Long itemId,
                                                @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("POST /items/{}/comment", itemId);
        return itemClient.createComment(userId, itemId, commentRequestDto);
    }
}
