package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final UserService userService;

    @Override
    public Item createItem(Long userId, ItemDto itemDto) {
        userService.findUserById(userId);
        return itemStorage.createItem(ItemMapper.toItem(userStorage.findUserById(userId), itemDto));
    }

    @Override
    public Item updateItem(Long itemId, Long userId, ItemDto itemDto) {
        userService.findUserById(userId);

        if (!Objects.equals(userId, itemStorage.findItemById(itemId).getOwner().getId())) {
            log.warn("Внесение изменений доступно владельцу");
            throw new ValidationException("Внесение изменений доступно владельцу");
        }

        User user = userStorage.findUserById(userId);
        Item item = ItemMapper.toItem(itemId, user, itemDto);
        return itemStorage.updateItem(userId, item);
    }

    @Override
    public Item findItemById(Long itemId) {
        return itemStorage.findItemById(itemId);
    }

    @Override
    public List<Item> findUserItems(Long userId) {
        return itemStorage.findUserItems().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return itemStorage.searchItem().stream()
                .filter(Objects::nonNull)
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase()))
                )
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .collect(Collectors.toList());
    }
}
