package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.RentalStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingResponseDto createBooking(Long userId, BookingRequestDto bookingRequestDto) {
        User booker = validateUser(userId);
        Item item = validateItem(bookingRequestDto.getItemId());

        if (Objects.equals(item.getOwner().getId(), userId)) {
            log.warn("Владелец не может бронировать свою вещь");
            throw new ValidationException("Владелец не может бронировать свою вещь");
        }
        if (!item.getAvailable()) {
            log.warn("Данная вещь недоступна для бронирования");
            throw new ValidationException("Данная вещь недоступна для бронирования");
        }

        if (!bookingRequestDto.getStart().isBefore(bookingRequestDto.getEnd())) {
            log.warn("Период указан неверно");
            throw new ValidationException("Период указан неверно");
        }

        RentalStatus status = RentalStatus.WAITING;

        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(bookingRequestDto, item, booker, status)));
    }

    @Override
    public BookingResponseDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        validateForbiddenUser(userId);
        Booking booking = validateBooking(bookingId);

        if (!Objects.equals(userId, booking.getItem().getOwner().getId())) {
            log.warn("Обновить аренду может только владелец вещи");
            throw new ValidationException("Обновить аренду может только владелец вещи");
        }

        if (!Objects.equals(booking.getStatus(), RentalStatus.WAITING)) {
            log.warn("Статус уже был установлен");
            throw new ValidationException("Нельзя изменить статус: бронирование уже подтверждено или отклонено");
        }

        booking.setStatus(approved ? RentalStatus.APPROVED : RentalStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto findBookingById(Long userId, Long bookingId) {
        validateUser(userId);
        Booking booking = validateBooking(bookingId);

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId) && !Objects.equals(booking.getBooker().getId(), userId)) {
            log.warn("Данный пользователь не может получить информацию по бронированию");
            throw new ValidationException("Данный пользователь не может получить информацию по бронированию");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingResponseDto> findUserBookings(Long userId, String state) {
        User booker = validateUser(userId);

        List<Booking> bookerBookings;
        Sort sortDescByStart = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case "CURRENT":
                bookerBookings = bookingRepository.findCurrentBookings(booker, sortDescByStart);
                break;
            case "PAST":
                bookerBookings = bookingRepository.findPastBookings(booker, sortDescByStart);
                break;
            case "FUTURE":
                bookerBookings = bookingRepository.findFutureBookings(booker, sortDescByStart);
                break;
            case "WAITING":
                bookerBookings = bookingRepository.findByBookerAndStatus(booker, RentalStatus.WAITING, sortDescByStart);
                break;
            case "REJECTED":
                bookerBookings = bookingRepository.findByBookerAndStatus(booker, RentalStatus.REJECTED, sortDescByStart);
                break;
            case "ALL":
            default:
                bookerBookings = bookingRepository.findByBooker(booker, sortDescByStart);
                break;
        }

        return bookerBookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> findOwnerReservedItems(Long userId, String state) {
        User owner = validateUser(userId);

        List<Booking> ownerBookings;
        Sort sortDescByStart = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case "CURRENT":
                ownerBookings = bookingRepository.findCurrentItemsBookings(owner, sortDescByStart);
                break;
            case "PAST":
                ownerBookings = bookingRepository.findPastItemsBookings(owner, sortDescByStart);
                break;
            case "FUTURE":
                ownerBookings = bookingRepository.findFutureItemsBookings(owner, sortDescByStart);
                break;
            case "WAITING":
                ownerBookings = bookingRepository.findByItemOwnerAndStatus(owner, RentalStatus.WAITING, sortDescByStart);
                break;
            case "REJECTED":
                ownerBookings = bookingRepository.findByItemOwnerAndStatus(owner, RentalStatus.REJECTED, sortDescByStart);
                break;
            case "ALL":
            default:
                ownerBookings = bookingRepository.findByItemOwner(owner, sortDescByStart);
                break;
        }

        return ownerBookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void validateForbiddenUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Пользователю с данным id: " + userId + " доступ запрещен"));
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с данным id: " + userId + " не найден"));
    }

    private Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с данным id: " + itemId + " не найден"));
    }

    private Booking validateBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с данным id:" + bookingId + " не найдено"));
    }
}
