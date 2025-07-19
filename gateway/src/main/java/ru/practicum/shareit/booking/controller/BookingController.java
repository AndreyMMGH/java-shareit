package ru.practicum.shareit.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDto;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        log.info("Создание бронирования {}, userId={}", bookingRequestDto, userId);
        return bookingClient.createBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("bookingId") Long bookingId, @RequestParam(name = "approved") Boolean approved) {
        log.info("Идентификатор бронирования {} по идентификатору пользователя {}", bookingId, userId);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable Long bookingId) {
        log.info("Забронировать {}, userId={}", bookingId, userId);
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Получить бронирования {}.", state);
        return bookingClient.findUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findOwnerReservedItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Получить зарезервированные товары владельца{}.", state);
        return bookingClient.findOwnerReservedItems(userId, state);
    }
}
