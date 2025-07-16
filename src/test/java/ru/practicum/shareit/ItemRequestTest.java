package ru.practicum.shareit;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ContextConfiguration(classes = ShareItApp.class)
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestTest {
    private final JacksonTester<ItemRequestDto> json;
    private final JacksonTester<ItemResponseDto> jsonItemResponseDto;
    private final JacksonTester<ItemDto> jsonItemDto;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldReturnItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                "Ищу строительный пылесос",
                1L
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Ищу строительный пылесос");
        assertThat(result).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
    }

    @Test
    void shouldReturnItemResponseDto() throws Exception {
        ItemResponseDto itemResponseDto = new ItemResponseDto(
                2L,
                "нужен фотоаппарат",
                3L,
                LocalDateTime.of(2025, 7, 16, 18, 30, 0),
                List.of()
        );

        JsonContent<ItemResponseDto> result = jsonItemResponseDto.write(itemResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("нужен фотоаппарат");
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-07-16T18:30:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }

    @Test
    void shouldReturnItemResponseDtoAndItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(
                5L,
                "Canon 500d",
                "Зеркальный фотоаппарат",
                true,
                10L
        );

        ItemResponseDto itemResponseDto = new ItemResponseDto(
                2L,
                "нужен фотоаппарат",
                3L,
                LocalDateTime.of(2025, 7, 16, 18, 30, 0),
                List.of(itemDto)
        );

        JsonContent<ItemResponseDto> result = jsonItemResponseDto.write(itemResponseDto);
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
    void shouldDeserializeItemResponseDtoFromJson() throws Exception {
        String json = """
                {
                  "id": 2,
                  "description": "нужен фотоаппарат",
                  "userId": 3,
                  "created": "2025-07-16T18:30:00",
                  "items": []
                }
                """;

        ItemResponseDto result = jsonItemResponseDto.parse(json).getObject();

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getDescription()).isEqualTo("нужен фотоаппарат");
        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2025, 7, 16, 18, 30, 0));
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void shouldSerializeAndValidateItemRequestDto() throws Exception {
        ItemRequestDto validDto = new ItemRequestDto("Нужен фотоаппарат", 1L);

        JsonContent<ItemRequestDto> validJson = json.write(validDto);
        assertThat(validJson).extractingJsonPathStringValue("$.description").isEqualTo("Нужен фотоаппарат");
        assertThat(validJson).extractingJsonPathNumberValue("$.requestorId").isEqualTo(1);
        assertThat(validator.validate(validDto)).isEmpty();

        ItemRequestDto invalidDto = new ItemRequestDto(" ", null);
        var violations = validator.validate(invalidDto);

        assertThat(violations).hasSize(2);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("requestorId"));
    }

    @Test
    void shouldFailValidationWhenDescriptionTooLong() {
        String longDescription = "тест".repeat(2001);
        ItemRequestDto itemRequestDto = new ItemRequestDto(longDescription, 1L);

        var violations = validator.validate(itemRequestDto);
        assertThat(violations).hasSize(1);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }
}

