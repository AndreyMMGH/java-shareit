package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ContextConfiguration(classes = ShareItApp.class)
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentRequestAndResponseDtoTest {
    private final JacksonTester<CommentRequestDto> jsonCommentRequestDto;
    private final JacksonTester<CommentResponseDto> jsonCommentResponseDto;

    @Test
    void shouldReturnCommentRequestDto() throws Exception {
        CommentRequestDto commentRequestDto = new CommentRequestDto(
                "Все понравилось. Рекомендую!"
        );

        JsonContent<CommentRequestDto> resultCommentRequestDto = jsonCommentRequestDto.write(commentRequestDto);

        assertThat(resultCommentRequestDto).extractingJsonPathStringValue("$.text").isEqualTo("Все понравилось. Рекомендую!");
    }

    @Test
    void shouldReturnCommentResponseDto() throws Exception {
        CommentResponseDto commentResponseDto = new CommentResponseDto(
                10L,
                "Все понравилось. Рекомендую!",
                "Александра Иванова",
                LocalDateTime.of(2025, 7, 19, 17, 58, 0),
                2L
        );

        JsonContent<CommentResponseDto> resultCommentResponseDto = jsonCommentResponseDto.write(commentResponseDto);

        assertThat(resultCommentResponseDto).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(resultCommentResponseDto).extractingJsonPathStringValue("$.text").isEqualTo("Все понравилось. Рекомендую!");
        assertThat(resultCommentResponseDto).extractingJsonPathStringValue("$.authorName").isEqualTo("Александра Иванова");
        assertThat(resultCommentResponseDto).extractingJsonPathStringValue("$.created").isEqualTo("2025-07-19T17:58:00");
        assertThat(resultCommentResponseDto).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
    }

    @Test
    void shouldReturnDeserializeCommentRequestDtoFromJson() throws Exception {
        String json = """
                {
                  "text": "Все понравилось. Рекомендую!"
                }
                """;

        CommentRequestDto dto = jsonCommentRequestDto.parse(json).getObject();

        assertThat(dto.getText()).isEqualTo("Все понравилось. Рекомендую!");
    }

    @Test
    void shouldReturnSerializeCommentResponseDtoWithNullFields() throws Exception {
        CommentResponseDto commentResponseDto = new CommentResponseDto(
                10L,
                "Все понравилось. Рекомендую!",
                null,
                null,
                2L
        );

        JsonContent<CommentResponseDto> result = jsonCommentResponseDto.write(commentResponseDto);

        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isNull();
        assertThat(result).extractingJsonPathStringValue("$.created").isNull();
    }
}
