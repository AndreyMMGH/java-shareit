package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ItemDtoValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void mustFailValidationWhenNameIsBlank() {
        ItemDto dto = new ItemDto(1L, "  ", "Полупрофессиональная модель фотоаппарата", true, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void mustFailValidationWhenDescriptionIsBlank() {
        ItemDto dto = new ItemDto(1L, "Canon 500d", " ", true, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description"));
    }

    @Test
    void mustFailValidationWhenAvailableIsNull() {
        ItemDto dto = new ItemDto(1L, "Canon 500d", "Полупрофессиональная модель фотоаппарата", null, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("available"));
    }

    @Test
    void mustPassValidationForValidDto() {
        ItemDto dto = new ItemDto(1L, "Canon 500d", "Полупрофессиональная модель фотоаппарата", true, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}
