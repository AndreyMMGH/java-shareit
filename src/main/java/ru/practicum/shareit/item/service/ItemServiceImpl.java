package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = validateUser(userId);

        return ItemMapper.toItemDto(itemRepository.save(
                ItemMapper.toItem(user, itemDto))
        );
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        validateUser(userId);

        Item item = validateItem(itemId);

        if (!Objects.equals(userId, item.getOwner().getId())) {
            log.warn("Внесение изменений доступно владельцу");
            throw new ValidationException("Внесение изменений доступно владельцу");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findItemById(Long itemId) {
        Item item = validateItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemBookingDto> findUserItems(Long userId) {
        User owner = validateUser(userId);
        List<Item> items = itemRepository.findAllByOwner(owner);

        return items.stream()
                .map(item -> {
                    Booking lastBooking = bookingRepository.findLastBooking(item.getId()).stream()
                            .findFirst()
                            .orElse(null);
                    Booking nextBooking = bookingRepository.findNextBooking(item.getId()).stream()
                            .findFirst()
                            .orElse(null);

                    return ItemMapper.toItemBookingDto(
                            item,
                            BookingMapper.toSimplifiedBookingDto(lastBooking),
                            BookingMapper.toSimplifiedBookingDto(nextBooking)
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.search(text).stream()
                .filter(item -> Objects.equals(item.getAvailable(), Boolean.TRUE))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id: " + userId + " не найден"));
    }

    private Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Элемент с данным id: " + itemId + " не найден"));
    }
}
