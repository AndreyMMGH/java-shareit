package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemDto itemDto;
    private ItemDto updatedItemDto;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();

        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        itemDto = new ItemDto(
                1L,
                "Karcher WD 2 Plus",
                "Строительный пылесос",
                true,
                2L
        );

        updatedItemDto = new ItemDto(
                1L,
                "Karcher WD 2 Plus",
                "Промышленный пылесос",
                true,
                2L
        );
    }

    @Test
    void mustCreateItem() throws Exception {
        when(itemService.createItem(eq(1L), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Karcher WD 2 Plus")))
                .andExpect(jsonPath("$.description", is("Строительный пылесос")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.requestId", is(2)));
    }

    @Test
    void mustUpdateItem() throws Exception {
        ItemDto patchDto = new ItemDto(
                null,
                null,
                "Промышленный пылесос",
                null,
                null
        );

        when(itemService.updateItem(eq(1L), eq(1L), any()))
                .thenReturn(updatedItemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(patchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Karcher WD 2 Plus")))
                .andExpect(jsonPath("$.description", is("Промышленный пылесос")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.requestId", is(2)));
    }

    @Test
    void mustReturnItemById() throws Exception {
        ItemBookingDto itemBookingDto = new ItemBookingDto(
                1L,
                "Karcher WD 2 Plus",
                "Строительный пылесос",
                true,
                null,
                null,
                List.of()
        );

        when(itemService.findItemById(eq(1L), eq(1L)))
                .thenReturn(itemBookingDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Karcher WD 2 Plus")))
                .andExpect(jsonPath("$.description", is("Строительный пылесос")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.lastBooking").doesNotExist())
                .andExpect(jsonPath("$.nextBooking").doesNotExist())
                .andExpect(jsonPath("$.comments", is(empty())));
    }

    @Test
    void mustReturnUserItems() throws Exception {
        List<ItemBookingDto> items = List.of(
                new ItemBookingDto(
                        1L,
                        "Karcher WD 2 Plus",
                        "Строительный пылесос",
                        true,
                        null,
                        null,
                        List.of()
                ),
                new ItemBookingDto(
                        2L,
                        "Bosch GSB 13 RE",
                        "Ударная дрель",
                        true,
                        null,
                        null,
                        List.of()
                )
        );

        when(itemService.findUserItems(1L))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Karcher WD 2 Plus")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Bosch GSB 13 RE")));
    }

    @Test
    void mustSearchItemsByText() throws Exception {
        List<ItemDto> searchResults = List.of(
                new ItemDto(
                        1L,
                        "Bosch GSB 13 RE",
                        "Ударная дрель",
                        true,
                        null
                ),
                new ItemDto(
                        2L,
                        "Makita HP1631K",
                        "Дрель с ударным механизмом",
                        true,
                        null
                )
        );

        when(itemService.searchItem("дрель")).thenReturn(searchResults);

        mvc.perform(get("/items/search")
                        .param("text", "дрель")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Bosch GSB 13 RE")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Makita HP1631K")));
    }

    @Test
    void mustReturnComment() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;

        CommentRequestDto requestDto = new CommentRequestDto("Все хорошо!");

        CommentResponseDto responseDto = new CommentResponseDto(
                1L,
                "Все хорошо!",
                "Макс Иванов",
                LocalDateTime.of(2025, 7, 20, 11, 30),
                1L
        );

        when(itemService.createComment(eq(userId), eq(itemId), any()))
                .thenReturn(responseDto);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Все хорошо!")))
                .andExpect(jsonPath("$.authorName", is("Макс Иванов")))
                .andExpect(jsonPath("$.created", is("2025-07-20T11:30:00")));
    }
}
