package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.SimplifiedBookingDto;
import ru.practicum.shareit.booking.status.RentalStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ContextConfiguration(classes = ShareItApp.class)
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRequestAndResponseDtoTest {
    private final JacksonTester<BookingRequestDto> jsonBookingRequestDto;
    private final JacksonTester<BookingResponseDto> jsonBookingResponseDto;
    private final JacksonTester<ItemDto> jsonItemDto;
    private final JacksonTester<UserDto> jsonUserDto;
    private final JacksonTester<SimplifiedBookingDto> jsonSimplifiedBookingDto;

    @Test
    void shouldReturnBookingRequestDto() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L,
                LocalDateTime.of(2025, 7, 20, 10, 0),
                LocalDateTime.of(2025, 7, 20, 12, 0)
        );
        JsonContent<BookingRequestDto> result = jsonBookingRequestDto.write(bookingRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-07-20T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-07-20T12:00:00");
    }

    @Test
    void shouldReturnBookingResponseDto() throws Exception {
        UserDto userDto = new UserDto(
                1L,
                "Макс Иванов",
                "Max@mail.ru"
        );

        ItemDto itemDto = new ItemDto(
                1L,
                "Canon 500d",
                "Полупрофессиональная модель фотоаппарата",
                true,
                5L
        );

        BookingResponseDto bookingResponseDto = new BookingResponseDto(
                1L,
                LocalDateTime.of(2025, 7, 20, 10, 0),
                LocalDateTime.of(2025, 7, 20, 12, 0),
                itemDto,
                userDto,
                RentalStatus.APPROVED
        );

        JsonContent<UserDto> resultUserDto = jsonUserDto.write(userDto);
        JsonContent<ItemDto> resultItemDto = jsonItemDto.write(itemDto);
        JsonContent<BookingResponseDto> resultBookingResponseDto = jsonBookingResponseDto.write(bookingResponseDto);

        assertThat(resultBookingResponseDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultBookingResponseDto).extractingJsonPathStringValue("$.start").isEqualTo("2025-07-20T10:00:00");
        assertThat(resultBookingResponseDto).extractingJsonPathStringValue("$.end").isEqualTo("2025-07-20T12:00:00");
        assertThat(resultItemDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultItemDto).extractingJsonPathStringValue("$.name").isEqualTo("Canon 500d");
        assertThat(resultItemDto).extractingJsonPathStringValue("$.description").isEqualTo("Полупрофессиональная модель фотоаппарата");
        assertThat(resultItemDto).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(resultItemDto).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
        assertThat(resultUserDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultUserDto).extractingJsonPathStringValue("$.name").isEqualTo("Макс Иванов");
        assertThat(resultUserDto).extractingJsonPathStringValue("$.email").isEqualTo("Max@mail.ru");
        assertThat(resultBookingResponseDto).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    @Test
    void shouldReturnSimplifiedBookingDto() throws Exception {
        SimplifiedBookingDto simplifiedBookingDto = new SimplifiedBookingDto(
                1L,
                1L
        );

        JsonContent<SimplifiedBookingDto> resultSimplifiedBookingDto = jsonSimplifiedBookingDto.write(simplifiedBookingDto);

        assertThat(resultSimplifiedBookingDto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(resultSimplifiedBookingDto).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }

    @Test
    void shouldDeserializeBookingRequestDto() throws Exception {
        String json = """
                    {
                        "itemId": 1,
                        "start": "2025-07-20T10:00:00",
                        "end": "2025-07-20T12:00:00"
                    }
                """;

        BookingRequestDto expected = new BookingRequestDto(
                1L,
                LocalDateTime.of(2025, 7, 20, 10, 0),
                LocalDateTime.of(2025, 7, 20, 12, 0)
        );

        assertThat(jsonBookingRequestDto.parse(json)).usingRecursiveComparison().isEqualTo(expected);
    }
}
