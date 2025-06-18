package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    ItemDto findItemById(Long itemId);

    List<ItemDto> findUserItems(Long userId);

    List<ItemDto> searchItem(String text);
}
