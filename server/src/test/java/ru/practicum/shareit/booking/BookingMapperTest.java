package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.SimplifiedBookingDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.RentalStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BookingMapperTest {
    @Test
    void shouldMapToBooking() {
        LocalDateTime start = LocalDateTime.of(2025, 7, 21, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 7, 22, 10, 0);

        BookingRequestDto requestDto = new BookingRequestDto(
                1L,
                start,
                end
        );

        User booker = new User(
                1L,
                "Макc Иванов",
                "Max@mail.ru"
        );

        User owner = new User(
                2L,
                "Петр Петров",
                "Petr@mail.ru"
        );

        Item item = new Item(
                3L,
                "Canon 500d",
                "Зеркальный фотоаппарат",
                true,
                owner,
                null
        );

        RentalStatus status = RentalStatus.WAITING;

        Booking booking = BookingMapper.toBooking(requestDto, item, booker, status);

        assertThat(booking.getId()).isNull();
        assertThat(booking.getStart()).isEqualTo(start);
        assertThat(booking.getEnd()).isEqualTo(end);
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getStatus()).isEqualTo(RentalStatus.WAITING);
    }

    @Test
    void mustMapToSimplifiedBookingDto() {
        User owner = new User(
                1L,
                "Макс Иванов",
                "Max@mail.ru"
        );

        Item item = new Item(
                3L,
                "Canon 500d",
                "Зеркальный фотоаппарат",
                true,
                owner,
                null
        );
        Booking booking = new Booking(
                10L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                item,
                owner,
                RentalStatus.APPROVED
        );

        SimplifiedBookingDto dto = BookingMapper.toSimplifiedBookingDto(booking);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getBookerId()).isEqualTo(1L);
    }

    @Test
    void mustReturnNullSimplifiedBookingDto() {
        SimplifiedBookingDto simplifiedBookingDto = BookingMapper.toSimplifiedBookingDto(null);

        assertThat(simplifiedBookingDto).isNull();
    }
}


