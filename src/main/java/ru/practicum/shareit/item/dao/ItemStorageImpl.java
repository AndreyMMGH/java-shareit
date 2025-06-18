package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
public class ItemStorageImpl implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Item updateItem) {
        if (!items.containsKey(updateItem.getId())) {
            log.warn("Обновляемая вещь не найдена");
            throw new NotFoundException("Обновляемая вещь не найдена");
        } else {
            Item oldItem = items.get(updateItem.getId());

            if (updateItem.getName() == null) {
                updateItem.setName(oldItem.getName());
            }
            if (updateItem.getDescription() == null) {
                updateItem.setDescription(oldItem.getDescription());
            }
            if (updateItem.getAvailable() == null) {
                updateItem.setAvailable(oldItem.getAvailable());
            }
        }

        items.put(updateItem.getId(), updateItem);
        return updateItem;
    }

    @Override
    public Item findItemById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> findUserItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> searchItem() {
        return new ArrayList<>(items.values());
    }

    private Long getId() {
        long lastId = items.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
