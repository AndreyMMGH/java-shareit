package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Long userId, ItemDto itemDto);

    Item updateItem(Long itemId, Long userId, ItemDto itemDto);

    Item findItemById(Long itemId);

    List<Item> findUserItems(Long userId);

    List<Item> searchItem(String text);
}
