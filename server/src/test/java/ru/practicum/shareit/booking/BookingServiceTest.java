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
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.RentalStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

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
    private Long itemId;

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

        ownerId = owner.getId();
        bookerId = booker.getId();
        bookingId = booking.getId();
        itemId = item.getId();
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

    @Test
    void shouldCreateBooking() {
        BookingRequestDto dto = new BookingRequestDto(
                itemId,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusDays(2)
        );

        BookingResponseDto result = bookingService.createBooking(bookerId, dto);
        assertThat(result.getItem().getId()).isEqualTo(itemId);
        assertThat(result.getStatus()).isEqualTo(RentalStatus.WAITING);
    }

    @Test
    void shouldThrowExceptionWhenBookerIsOwner() {
        BookingRequestDto dto = new BookingRequestDto(
                itemId,
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusDays(2)
        );

        ValidationException ve = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(ownerId, dto));
        assertThat(ve.getMessage()).isEqualTo("Владелец не может бронировать свою вещь");
    }

    @Test
    void shouldThrowExceptionWhenItemUnavailable() {
        Item item = em.find(Item.class, itemId);
        item.setAvailable(false);
        em.flush();

        BookingRequestDto dto = new BookingRequestDto(
                itemId,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1)
        );

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookerId, dto));
        assertThat(validationException.getMessage()).isEqualTo("Данная вещь недоступна для бронирования");
    }

    @Test
    void shouldThrowExceptionWhenStartAfterEnd() {
        BookingRequestDto dto = new BookingRequestDto(
                itemId,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusHours(1)
        );

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookerId, dto));
        assertThat(validationException.getMessage()).isEqualTo("Период указан неверно");
    }

    @Test
    void shouldFindBookingByBooker() {
        BookingResponseDto result = bookingService.findBookingById(bookerId, bookingId);
        assertThat(result.getId()).isEqualTo(bookingId);
    }

    @Test
    void shouldFindBookingByOwner() {
        BookingResponseDto result = bookingService.findBookingById(ownerId, bookingId);
        assertThat(result.getId()).isEqualTo(bookingId);
    }

    @Test
    void shouldThrowExceptionWhenOtherUserRequestsBooking() {
        User otherUser = new User();
        otherUser.setName("Макс Иванов");
        otherUser.setEmail("Max@mail.com");
        em.persist(otherUser);
        em.flush();

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.findBookingById(otherUser.getId(), bookingId));
        assertThat(validationException.getMessage()).isEqualTo("Данный пользователь не может получить информацию по бронированию");
    }

    @Test
    void shouldReturnUserBookingsAll() {
        List<BookingResponseDto> bookings = bookingService.findUserBookings(bookerId, "ALL");
        assertThat(bookings.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnOwnerBookingsAll() {
        List<BookingResponseDto> bookings = bookingService.findOwnerReservedItems(ownerId, "ALL");
        assertThat(bookings.size()).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionIfUserNotFoundInFindUserBookings() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.findUserBookings(999L, "ALL"));
        assertThat(notFoundException.getMessage()).contains("Пользователь с данным id: " + 999L + " не найден");
    }

    @Test
    void shouldThrowExceptionIfUserNotFoundInFindOwnerReservedItems() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.findOwnerReservedItems(999L, "ALL"));
        assertThat(notFoundException.getMessage()).contains("Пользователь с данным id: " + 999L + " не найден");
    }
}
