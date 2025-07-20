package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.SimplifiedBookingDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ContextConfiguration(classes = ShareItApp.class)
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemAndBookingDtoTest {
    private final JacksonTester<ItemDto> jsonItemDto;
    private final JacksonTester<CommentResponseDto> jsonCommentResponseDto;
    private final JacksonTester<SimplifiedBookingDto> jsonSimplifiedBookingDto;
    private final JacksonTester<ItemBookingDto> jsonItemBookingDto;


    @Test
    void shouldReturnItemDto() throws Exception
    {
        ItemDto itemDto = new ItemDto(
                1L,
                "Canon 500d",
                "Полупрофессиональная модель фотоаппарата",
                true,
                5L
        );

        JsonContent<ItemDto> result = jsonItemDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Canon 500d");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Полупрофессиональная модель фотоаппарата");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
    }

    @Test
    void shouldReturnItemBookingDto() throws Exception
    {

        CommentResponseDto commentResponseDto = new CommentResponseDto(
                10L,
                "Все понравилось. Рекомендую!",
                "Александра Иванова",
                LocalDateTime.of(2025, 7, 19, 17, 58, 0),
                2L
        );

        SimplifiedBookingDto simplifiedBookingDto = new SimplifiedBookingDto(
                1L,
                1L
        );

        SimplifiedBookingDto simplifiedBookingDto2 = new SimplifiedBookingDto(
                3L,
                2L
        );

        ItemBookingDto itemBookingDto = new ItemBookingDto(
                2L,
                "Canon 500d",
                "Полупрофессиональная модель фотоаппарата",
                true,
                simplifiedBookingDto,
                simplifiedBookingDto2,
                List.of(commentResponseDto)
        );

        JsonContent<CommentResponseDto> resultCommentResponseDto = jsonCommentResponseDto.write(commentResponseDto);
        JsonContent<SimplifiedBookingDto> resultItemDtoSimplifiedBookingDto = jsonSimplifiedBookingDto.write(simplifiedBookingDto);
        JsonContent<SimplifiedBookingDto> resultItemDtoSimplifiedBookingDto2 = jsonSimplifiedBookingDto.write(simplifiedBookingDto2);
        JsonContent<ItemBookingDto> resultItemBookingDto = jsonItemBookingDto.write(itemBookingDto);

        assertThat(resultItemBookingDto).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(resultItemBookingDto).extractingJsonPathStringValue("$.name").isEqualTo("Canon 500d");
        assertThat(resultItemBookingDto).extractingJsonPathStringValue("$.description").isEqualTo("Полупрофессиональная модель фотоаппарата");
        assertThat(resultItemBookingDto).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(resultItemDtoSimplifiedBookingDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultItemDtoSimplifiedBookingDto).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(resultItemDtoSimplifiedBookingDto2).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(resultItemDtoSimplifiedBookingDto2).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(resultCommentResponseDto).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(resultCommentResponseDto).extractingJsonPathStringValue("$.text").isEqualTo("Все понравилось. Рекомендую!");
        assertThat(resultCommentResponseDto).extractingJsonPathStringValue("$.authorName").isEqualTo("Александра Иванова");
        assertThat(resultCommentResponseDto).extractingJsonPathStringValue("$.created").isEqualTo("2025-07-19T17:58:00");
        assertThat(resultCommentResponseDto).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
    }

    @Test
    void shouldDeserializeItemBookingDtoFromJson() throws Exception
    {
        String json = """
                {
                  "id": 2,
                  "name": "Canon 500d",
                  "description": "Полупрофессиональная модель фотоаппарата",
                  "available": true,
                  "lastBooking": {
                    "id": 1,
                    "bookerId": 1
                  },
                  "nextBooking": {
                    "id": 3,
                    "bookerId": 2
                  },
                  "comments": [
                    {
                      "id": 10,
                      "text": "Все понравилось. Рекомендую!",
                      "authorName": "Александра Иванова",
                      "created": "2025-07-19T17:58:00",
                      "itemId": 2
                    }
                  ]
                }
                """;

        ItemBookingDto result = jsonItemBookingDto.parse(json).getObject();

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Canon 500d");
        assertThat(result.getDescription()).isEqualTo("Полупрофессиональная модель фотоаппарата");
        assertThat(result.getAvailable()).isTrue();

        assertThat(result.getLastBooking().getId()).isEqualTo(1L);
        assertThat(result.getLastBooking().getBookerId()).isEqualTo(1L);
        assertThat(result.getNextBooking().getId()).isEqualTo(3L);
        assertThat(result.getNextBooking().getBookerId()).isEqualTo(2L);

        assertThat(result.getComments()).hasSize(1);
        assertThat(result.getComments().get(0).getText()).isEqualTo("Все понравилось. Рекомендую!");
    }

    @Test
    void shouldSerializeItemBookingDtoWithNullFields() throws Exception
    {
        ItemBookingDto itemBookingDto = new ItemBookingDto(
                2L,
                "Canon 500d",
                "Полупрофессиональная модель фотоаппарата",
                true,
                null,
                null,
                null
        );

        JsonContent<ItemBookingDto> result = jsonItemBookingDto.write(itemBookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Canon 500d");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Полупрофессиональная модель фотоаппарата");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
    }
}
