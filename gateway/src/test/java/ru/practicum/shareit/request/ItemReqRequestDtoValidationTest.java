package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ItemReqRequestDtoValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void mustFailValidationWhenDescriptionIsBlank() {
        ItemRequestDto dto = new ItemRequestDto("   ", 1L);

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    void mustFailValidationWhenDescriptionTooLong() {
        String longDescription = "test".repeat(2001);
        ItemRequestDto dto = new ItemRequestDto(longDescription, 1L);

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    void mustFailValidationWhenRequestorIdIsNull() {
        ItemRequestDto dto = new ItemRequestDto("Описание для теста", null);

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("requestorId"));
    }

    @Test
    void mustPassValidationForValidDto() {
        ItemRequestDto dto = new ItemRequestDto("Описание для теста", 1L);

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
