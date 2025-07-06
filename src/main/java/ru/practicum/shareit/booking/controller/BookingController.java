package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("POST /bookings");
        return bookingService.createBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("bookingId") Long bookingId, @RequestParam(name = "approved") Boolean approved) {
        log.info("PATCH /bookings/{}", bookingId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("bookingId") Long bookingId) {
        log.info("GET /bookings/{}", bookingId);
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("GET /bookings?state={}.", state);
        return bookingService.findUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findOwnerReservedItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner?state={}.", state);
        return bookingService.findOwnerReservedItems(userId, state);
    }
}
