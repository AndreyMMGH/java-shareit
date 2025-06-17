package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("POST /items");
        return ItemMapper.toItemDto(itemService.createItem(userId, itemDto));
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId, @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/{}", itemId);
        return ItemMapper.toItemDto(itemService.updateItem(itemId, userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable("itemId") Long itemId) {
        log.info("GET /items/{}", itemId);
        return ItemMapper.toItemDto(itemService.findItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> findUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items");
        return itemService.findUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(name = "text") String text) {
        log.info("GET /items/search?text={}.", text);
        return itemService.searchItem(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
