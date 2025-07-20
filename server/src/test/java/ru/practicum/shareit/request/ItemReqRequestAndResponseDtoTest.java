package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ContextConfiguration(classes = ShareItApp.class)
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemReqRequestAndResponseDtoTest {
    private final JacksonTester<ItemReqRequestDto> json;
    private final JacksonTester<ItemReqResponseDto> jsonItemResponseDto;
    private final JacksonTester<ItemDto> jsonItemDto;

    @Test
    void shouldReturnItemReqRequestDto() throws Exception
    {
        ItemReqRequestDto itemReqRequestDto = new ItemReqRequestDto(
                "Ищу строительный пылесос",
                1L
        );

        JsonContent<ItemReqRequestDto> result = json.write(itemReqRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Ищу строительный пылесос");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
    }

    @Test
    void shouldReturnItemReqResponseDto() throws Exception
    {
        ItemReqResponseDto itemReqResponseDto = new ItemReqResponseDto(
                2L,
                "нужен фотоаппарат",
                3L,
                LocalDateTime.of(2025, 7, 16, 18, 30, 0),
                List.of()
        );

        JsonContent<ItemReqResponseDto> result = jsonItemResponseDto.write(itemReqResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("нужен фотоаппарат");
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-07-16T18:30:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }

    @Test
    void shouldReturnItemReqResponseDtoAndItemDto() throws Exception
    {
        ItemDto itemDto = new ItemDto(
                5L,
                "Canon 500d",
                "Зеркальный фотоаппарат",
                true,
                10L
        );

        ItemReqResponseDto itemReqResponseDto = new ItemReqResponseDto(
                2L,
                "нужен фотоаппарат",
                3L,
                LocalDateTime.of(2025, 7, 16, 18, 30, 0),
                List.of(itemDto)
        );

        JsonContent<ItemReqResponseDto> result = jsonItemResponseDto.write(itemReqResponseDto);
        JsonContent<ItemDto> resultItemDto = jsonItemDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("нужен фотоаппарат");
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-07-16T18:30:00");
        assertThat(resultItemDto).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(resultItemDto).extractingJsonPathStringValue("$.name").isEqualTo("Canon 500d");
        assertThat(resultItemDto).extractingJsonPathStringValue("$.description").isEqualTo("Зеркальный фотоаппарат");
        assertThat(resultItemDto).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(resultItemDto).extractingJsonPathNumberValue("$.requestId").isEqualTo(10);
    }

    @Test
    void shouldDeserializeItemReqResponseDtoFromJson() throws Exception
    {
        String json = """
                {
                  "id": 2,
                  "description": "нужен фотоаппарат",
                  "userId": 3,
                  "created": "2025-07-16T18:30:00",
                  "items": []
                }
                """;

        ItemReqResponseDto result = jsonItemResponseDto.parse(json).getObject();

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getDescription()).isEqualTo("нужен фотоаппарат");
        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2025, 7, 16, 18, 30, 0));
        assertThat(result.getItems()).isEmpty();
    }
}

