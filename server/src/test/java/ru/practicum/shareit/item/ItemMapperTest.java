package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemMapperTest {
    @Test
    void mustMapItemToItemDtoWithRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(42L);

        Item item = new Item(
                1L,
                "Canon 500d",
                "Зеркальный фотоаппарат",
                true, new User(), itemRequest
        );

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Canon 500d");
        assertThat(itemDto.getDescription()).isEqualTo("Зеркальный фотоаппарат");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(42L);
    }

    @Test
    void mustMapItemToItemDtoWithNullRequest() {
        Item item = new Item(
                1L,
                "Canon 500d",
                "Зеркальный фотоаппарат",
                false,
                new User(),
                null
        );

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    void mustMapItemDtoToItemWithoutRequest() {
        User user = new User(
                1L,
                "Макс Иванов",
                "Max@mail.com"
        );

        ItemDto itemDto = new ItemDto(
                null,
                "Canon 500d",
                "Зеркальный фотоаппарат",
                true,
                null
        );

        Item item = ItemMapper.toItem(user, itemDto);

        assertThat(item.getId()).isNull();
        assertThat(item.getName()).isEqualTo("Canon 500d");
        assertThat(item.getDescription()).isEqualTo("Зеркальный фотоаппарат");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(user);
        assertThat(item.getItemRequest()).isNull();
    }

    @Test
    void mustMapItemDtoToItemWithRequest() {
        User user = new User(
                20L,
                "Анна Иванова",
                "Anna@mail.com"
        );
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(100L);

        ItemDto itemDto = new ItemDto(
                null,
                "Canon 500d",
                "Зеркальный фотоаппарат",
                false,
                100L
        );

        Item item = ItemMapper.toItem(user, itemDto, itemRequest);

        assertThat(item.getName()).isEqualTo("Canon 500d");
        assertThat(item.getDescription()).isEqualTo("Зеркальный фотоаппарат");
        assertThat(item.getAvailable()).isFalse();
        assertThat(item.getOwner()).isEqualTo(user);
        assertThat(item.getItemRequest()).isEqualTo(itemRequest);
    }
}
