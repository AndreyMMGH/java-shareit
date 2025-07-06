package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.RentalStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking AS b WHERE b.booker = :booker AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    List<Booking> findCurrentBookings(@Param("booker") User booker, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.booker = :booker AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findPastBookings(@Param("booker") User booker, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.booker = :booker AND b.start > CURRENT_TIMESTAMP")
    List<Booking> findFutureBookings(@Param("booker") User booker, Sort sort);

    List<Booking> findByBookerAndStatus(User booker, RentalStatus status, Sort sort);

    List<Booking> findByBooker(User booker, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    List<Booking> findCurrentItemsBookings(@Param("owner") User owner, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findPastItemsBookings(@Param("owner") User owner, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner AND b.start > CURRENT_TIMESTAMP")
    List<Booking> findFutureItemsBookings(@Param("owner") User owner, Sort sort);

    List<Booking> findByItemOwnerAndStatus(User owner, RentalStatus status, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.item.owner = :owner")
    List<Booking> findByItemOwner(@Param("owner") User owner, Sort sort);

    Booking findFirstByBookerIdAndItemIdAndEndIsBefore(Long bookerId, Long itemId, LocalDateTime end, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.end <= CURRENT_TIMESTAMP")
    List<Booking> findLastBookingsForItems(@Param("itemIds") List<Long> itemIds, Sort sort);

    @Query("SELECT b FROM Booking b WHERE b.item.id IN :itemIds AND b.start >= CURRENT_TIMESTAMP")
    List<Booking> findNextBookingsForItems(@Param("itemIds") List<Long> itemIds, Sort sort);
}
