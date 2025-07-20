package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.RentalStatus;
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private BookingRequestDto bookingRequestDto;
    private ItemDto itemDto;
    private UserDto userDto;
    private BookingResponseDto bookingResponseDto;
    private BookingResponseDto bookingResponseDto2;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();

        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        bookingRequestDto = new BookingRequestDto(
                1L,
                LocalDateTime.of(2025, 7, 20, 10, 0),
                LocalDateTime.of(2025, 7, 20, 12, 0)
        );

        userDto = new UserDto(
                1L,
                "Макс Иванов",
                "Max@mail.ru"
        );

        itemDto = new ItemDto(
                1L,
                "Canon 500d",
                "Полупрофессиональная модель фотоаппарата",
                true,
                5L
        );

        bookingResponseDto = new BookingResponseDto(
                1L,
                LocalDateTime.of(2025, 7, 20, 10, 0),
                LocalDateTime.of(2025, 7, 20, 12, 0),
                itemDto,
                userDto,
                RentalStatus.APPROVED
        );

        bookingResponseDto2 = new BookingResponseDto(
                2L,
                LocalDateTime.of(2025, 7, 20, 13, 0),
                LocalDateTime.of(2025, 7, 20, 14, 0),
                null,
                null,
                RentalStatus.APPROVED
        );
    }

    @Test
    void mustCreateBooking() throws Exception {
        when(bookingService.createBooking(eq(1L), any()))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is("2025-07-20T10:00:00")))
                .andExpect(jsonPath("$.end", is("2025-07-20T12:00:00")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.name", is("Canon 500d")))
                .andExpect(jsonPath("$.item.description", is("Полупрофессиональная модель фотоаппарата")))
                .andExpect(jsonPath("$.item.available", is(true)))
                .andExpect(jsonPath("$.item.requestId", is(5)))
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.booker.name", is("Макс Иванов")))
                .andExpect(jsonPath("$.booker.email", is("Max@mail.ru")))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void mustUpdateBookingStatus() throws Exception {
        bookingResponseDto.setStatus(RentalStatus.REJECTED);

        when(bookingService.updateBooking(1L, 1L, false))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings/1")
                        .param("approved", "false")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is("2025-07-20T10:00:00")))
                .andExpect(jsonPath("$.end", is("2025-07-20T12:00:00")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.name", is("Canon 500d")))
                .andExpect(jsonPath("$.item.description", is("Полупрофессиональная модель фотоаппарата")))
                .andExpect(jsonPath("$.item.available", is(true)))
                .andExpect(jsonPath("$.item.requestId", is(5)))
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.booker.name", is("Макс Иванов")))
                .andExpect(jsonPath("$.booker.email", is("Max@mail.ru")))
                .andExpect(jsonPath("$.status", is("REJECTED")));
    }

    @Test
    void mustReturnBookingById() throws Exception {
        when(bookingService.findBookingById(1L, 1L))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.start", is("2025-07-20T10:00:00")))
                .andExpect(jsonPath("$.end", is("2025-07-20T12:00:00")))
                .andExpect(jsonPath("$.item.name", is("Canon 500d")))
                .andExpect(jsonPath("$.item.description", is("Полупрофессиональная модель фотоаппарата")))
                .andExpect(jsonPath("$.item.available", is(true)))
                .andExpect(jsonPath("$.item.requestId", is(5)))
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.booker.name", is("Макс Иванов")))
                .andExpect(jsonPath("$.booker.email", is("Max@mail.ru")))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void mustReturnUserBookings() throws Exception {
        List<BookingResponseDto> bookings = List.of(bookingResponseDto, bookingResponseDto2);

        when(bookingService.findUserBookings(1L, "ALL"))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is("2025-07-20T10:00:00")))
                .andExpect(jsonPath("$[0].end", is("2025-07-20T12:00:00")))
                .andExpect(jsonPath("$[0].item.name", is("Canon 500d")))
                .andExpect(jsonPath("$[0].item.description", is("Полупрофессиональная модель фотоаппарата")))
                .andExpect(jsonPath("$[0].item.available", is(true)))
                .andExpect(jsonPath("$[0].item.requestId", is(5)))
                .andExpect(jsonPath("$[0].booker.id", is(1)))
                .andExpect(jsonPath("$[0].booker.name", is("Макс Иванов")))
                .andExpect(jsonPath("$[0].booker.email", is("Max@mail.ru")))
                .andExpect(jsonPath("$[0].status", is("APPROVED")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].start", is("2025-07-20T13:00:00")))
                .andExpect(jsonPath("$[1].end", is("2025-07-20T14:00:00")))
                .andExpect(jsonPath("$[1].item").doesNotExist())
                .andExpect(jsonPath("$[1].booker").doesNotExist())
                .andExpect(jsonPath("$[1].status", is("APPROVED")));
    }

    @Test
    void mustReturnOwnerReservedItems() throws Exception {
        List<BookingResponseDto> bookings = List.of(bookingResponseDto, bookingResponseDto2);

        when(bookingService.findOwnerReservedItems(1L, "ALL"))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].start", is("2025-07-20T10:00:00")))
                .andExpect(jsonPath("$[0].end", is("2025-07-20T12:00:00")))
                .andExpect(jsonPath("$[0].item.name", is("Canon 500d")))
                .andExpect(jsonPath("$[0].item.description", is("Полупрофессиональная модель фотоаппарата")))
                .andExpect(jsonPath("$[0].item.available", is(true)))
                .andExpect(jsonPath("$[0].item.requestId", is(5)))
                .andExpect(jsonPath("$[0].booker.id", is(1)))
                .andExpect(jsonPath("$[0].booker.name", is("Макс Иванов")))
                .andExpect(jsonPath("$[0].booker.email", is("Max@mail.ru")))
                .andExpect(jsonPath("$[0].status", is("APPROVED")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].start", is("2025-07-20T13:00:00")))
                .andExpect(jsonPath("$[1].end", is("2025-07-20T14:00:00")))
                .andExpect(jsonPath("$[1].item").doesNotExist())
                .andExpect(jsonPath("$[1].booker").doesNotExist())
                .andExpect(jsonPath("$[1].status", is("APPROVED")));
    }
}
