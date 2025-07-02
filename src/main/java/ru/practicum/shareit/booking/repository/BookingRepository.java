package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.RentalStatus;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking AS b WHERE b.booker = :booker AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> findCurrentBookings(@Param("booker") User booker);

    @Query("SELECT b FROM Booking AS b WHERE b.booker = :booker AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findPastBookings(@Param("booker") User booker);

    @Query("SELECT b FROM Booking AS b WHERE b.booker = :booker AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findFutureBookings(@Param("booker") User booker);

    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, RentalStatus status);

    List<Booking> findByBookerOrderByStartDesc(User booker);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> findCurrentItemsBookings(@Param("owner") User owner);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND b.end < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findPastItemsBookings(@Param("owner") User owner);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findFutureItemsBookings(@Param("owner") User owner);

    List<Booking> findByItemOwnerAndStatusOrderByStartDesc(User owner, RentalStatus status);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner ORDER BY b.start DESC")
    List<Booking> findByItemOwnerOrderByStartDesc(@Param("owner") User owner);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = :itemId AND b.end < CURRENT_TIMESTAMP ORDER BY b.end DESC")
    List<Booking> findLastBooking(@Param("itemId") Long itemId);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = :itemId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start ASC")
    List<Booking> findNextBooking(@Param("itemId") Long itemId);
}
