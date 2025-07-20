package ru.practicum.shareit.request;

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
import ru.practicum.shareit.error.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemReqRequestDto;
import ru.practicum.shareit.request.dto.ItemReqResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;
    @InjectMocks
    private ItemRequestController itemRequestController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemReqRequestDto itemReqRequestDto;
    private ItemDto itemDto;
    private ItemReqResponseDto itemReqResponseDto;
    private ItemReqResponseDto itemReqResponseDto2;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();

        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        itemReqRequestDto = new ItemReqRequestDto(
                "Ищу строительный пылесос",
                1L
        );

        itemDto = new ItemDto(
                1L,
                "Karcher WD 2 Plus",
                "Строительный пылесос",
                true,
                2L
        );

        itemReqResponseDto = new ItemReqResponseDto(
                1L,
                "Ищу строительный пылесос",
                1L,
                LocalDateTime.of(2025, 7, 16, 19, 40, 0),
                List.of()
        );

        itemReqResponseDto2 = new ItemReqResponseDto(
                1L,
                "Ищу строительный пылесос",
                1L,
                LocalDateTime.of(2025, 7, 16, 19, 40, 0),
                List.of(itemDto)
        );
    }

    @Test
    void mustCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(eq(1L), any()))
                .thenReturn(itemReqResponseDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemReqRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is(itemReqRequestDto.getDescription())))
                .andExpect(jsonPath("$.userId", is(itemReqRequestDto.getRequestorId().intValue())))
                .andExpect(jsonPath("$.created", is("2025-07-16T19:40:00")))
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void mustFindListOfYourQueriesWithAnswers() throws Exception {
        when(itemRequestService.findListOfYourQueriesWithAnswers(1L))
                .thenReturn(List.of(itemReqResponseDto2));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Ищу строительный пылесос")))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].created", is("2025-07-16T19:40:00")))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items[0].id", is(1)))
                .andExpect(jsonPath("$[0].items[0].name", is("Karcher WD 2 Plus")))
                .andExpect(jsonPath("$[0].items[0].description", is("Строительный пылесос")))
                .andExpect(jsonPath("$[0].items[0].available", is(true)))
                .andExpect(jsonPath("$[0].items[0].requestId", is(2)));
    }

    @Test
    void mustFindListOfRequestsOtherUsers() throws Exception {
        when(itemRequestService.findListOfRequestsOtherUsers(1L, 0, 10))
                .thenReturn(List.of(itemReqResponseDto2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Ищу строительный пылесос")))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].created", is("2025-07-16T19:40:00")))
                .andExpect(jsonPath("$[0].items").isArray())
                .andExpect(jsonPath("$[0].items[0].id", is(1)))
                .andExpect(jsonPath("$[0].items[0].name", is("Karcher WD 2 Plus")))
                .andExpect(jsonPath("$[0].items[0].description", is("Строительный пылесос")))
                .andExpect(jsonPath("$[0].items[0].available", is(true)))
                .andExpect(jsonPath("$[0].items[0].requestId", is(2)));
    }

    @Test
    void mustFindYourQueryWithAnswers() throws Exception {
        when(itemRequestService.findYourQueryWithAnswers(1L, 1L))
                .thenReturn(itemReqResponseDto2);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Ищу строительный пылесос")))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.created", is("2025-07-16T19:40:00")))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].id", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("Karcher WD 2 Plus")));
    }

    @Test
    void mustReturn404IfItemRequestNotFound() throws Exception {
        when(itemRequestService.findYourQueryWithAnswers(1L, 99L))
                .thenThrow(new NotFoundException("Запрос не найден"));

        mvc.perform(get("/requests/99")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
