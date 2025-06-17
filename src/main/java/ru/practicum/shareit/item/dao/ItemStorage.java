package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Long userId, Item updateItem);

    Item findItemById(Long itemId);

    List<Item> findUserItems();

    List<Item> searchItem();
}
