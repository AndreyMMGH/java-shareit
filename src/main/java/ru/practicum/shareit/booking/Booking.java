package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@AllArgsConstructor
@Data
public class Booking {
    private Long id;
    private LocalDateTime bookingStartDate;
    private LocalDateTime endDateOfBooking;
    private Item item;
    private User booker;
    private RentalStatus rentalStatus;
}
