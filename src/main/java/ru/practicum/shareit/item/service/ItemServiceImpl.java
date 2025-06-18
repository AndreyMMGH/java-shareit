package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserStorage;

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

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userStorage.findUserById(userId);
        validateUser(user, userId);

        return ItemMapper.toItemDto(itemStorage.createItem(
                ItemMapper.toItem(user, itemDto))
        );
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        User user = userStorage.findUserById(userId);
        validateUser(user, userId);

        if (!Objects.equals(userId, itemStorage.findItemById(itemId).getOwner().getId())) {
            log.warn("Внесение изменений доступно владельцу");
            throw new ValidationException("Внесение изменений доступно владельцу");
        }

        Item item = ItemMapper.toItem(itemId, user, itemDto);
        return ItemMapper.toItemDto(itemStorage.updateItem(userId, item));
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.findItemById(itemId));
    }

    @Override
    public List<ItemDto> findUserItems(Long userId) {
        return itemStorage.findUserItems().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
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
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public void validateUser(User user, Long userId) {
        if (user == null) {
            log.warn("Пользователь с данным id {} не найден", userId);
            throw new NotFoundException("Пользователь с данным id: " + userId + " не найден");
        }
    }
}
