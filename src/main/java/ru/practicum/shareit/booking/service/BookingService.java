package ru.practicum.shareit.booking.service;


import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto findBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> findUserBookings(Long userId, String state);

    List<BookingResponseDto> findOwnerReservedItems(Long userId, String state);
}
