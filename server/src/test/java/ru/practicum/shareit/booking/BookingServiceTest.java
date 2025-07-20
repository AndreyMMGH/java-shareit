package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.RentalStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(
        classes = ShareItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final EntityManager em;
    private final BookingService bookingService;

    private Long ownerId;
    private Long bookerId;
    private Long bookingId;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Владимир Петухов");
        owner.setEmail("Vladimir@mail.ru");
        em.persist(owner);

        User booker = new User();
        booker.setName("Петр Васильев");
        booker.setEmail("Petr@mail.ru");
        em.persist(booker);

        ownerId = owner.getId();
        bookerId = booker.getId();

        Item item = new Item();
        item.setName("Canon 500d");
        item.setDescription("Зеркальный фотоаппарат");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusHours(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(RentalStatus.WAITING);
        em.persist(booking);

        em.flush();
        em.clear();

        bookingId = booking.getId();
    }

    @Test
    void shouldApproveBooking() {
        BookingResponseDto result = bookingService.updateBooking(ownerId, bookingId, true);

        assertThat(result.getId()).isEqualTo(bookingId);
        assertThat(result.getStatus()).isEqualTo(RentalStatus.APPROVED);
    }

    @Test
    void shouldRejectBooking() {
        BookingResponseDto result = bookingService.updateBooking(ownerId, bookingId, false);

        assertThat(result.getStatus()).isEqualTo(RentalStatus.REJECTED);
    }

    @Test
    void shouldThrowExceptionWhenNotOwner() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(bookerId, bookingId, true)
        );

        assertThat(exception.getMessage()).isEqualTo("Обновить аренду может только владелец вещи");
    }

    @Test
    void shouldThrowWhenStatusAlreadySet() {
        bookingService.updateBooking(ownerId, bookingId, true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(ownerId, bookingId, false)
        );

        assertThat(exception.getMessage()).isEqualTo("Нельзя изменить статус: бронирование уже подтверждено или отклонено");
    }
}
