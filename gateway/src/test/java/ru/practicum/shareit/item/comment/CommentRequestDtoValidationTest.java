package ru.practicum.shareit.item.comment;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CommentRequestDtoValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void mustFailValidationWhenTextIsBlank() {
        CommentRequestDto dto = new CommentRequestDto("  ");

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("text"));
    }

    @Test
    void mustFailValidationWhenTextIsTooLong() {
        String longText = "Тест".repeat(2001);
        CommentRequestDto dto = new CommentRequestDto(longText);

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("text"));
    }

    @Test
    void mustPassValidationWhenTextIsValid() {
        CommentRequestDto dto = new CommentRequestDto("Хорошая вещь. Все отлично!");

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
