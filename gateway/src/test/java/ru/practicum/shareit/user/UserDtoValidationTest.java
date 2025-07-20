package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class UserDtoValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void mustFailValidationWhenNameIsBlank() {
        UserDto userDto = new UserDto(1L, "", "Max@mail.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void mustFailValidationWhenEmailIsInvalid() {
        UserDto userDto = new UserDto(1L, "Макс Иванов", "Maxmail.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void mustFailValidationWhenEmailIsBlank() {
        UserDto userDto = new UserDto(1L, "Макс Иванов", "");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void mustPassValidationWhenAllFieldsAreValid() {
        UserDto userDto = new UserDto(1L, "Макс Иванов", "Max@mail.ru");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertThat(violations).isEmpty();
    }
}
