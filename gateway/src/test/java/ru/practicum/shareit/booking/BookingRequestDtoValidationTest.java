package ru.practicum.shareit.booking;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingRequestDtoValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void mustFailWhenItemIdIsNull() {
        BookingRequestDto dto = new BookingRequestDto(
                null,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusDays(1)
        );

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("itemId"));
    }

    @Test
    void mustFailWhenStartIsInPast() {
        BookingRequestDto dto = new BookingRequestDto(
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("start"));
    }

    @Test
    void mustFailWhenEndIsInPast() {
        BookingRequestDto dto = new BookingRequestDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().minusHours(1)
        );

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("end"));
    }

    @Test
    void mustFailWhenStartOrEndIsNull() {
        BookingRequestDto dto = new BookingRequestDto(
                1L,
                null,
                null
        );

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("start"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("end"));
    }

    @Test
    void mustPassValidation() {
        BookingRequestDto dto = new BookingRequestDto(
                1L,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusDays(1)
        );

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }
}
