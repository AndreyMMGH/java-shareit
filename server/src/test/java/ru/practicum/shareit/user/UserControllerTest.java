package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();

        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        userDto = new UserDto(
                1L,
                "Макс Иванов",
                "Max@mail.ru"
        );
    }

    @Test
    void mustCreateUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Макс Иванов")))
                .andExpect(jsonPath("$.email", is("Max@mail.ru")));
    }

    @Test
    void mustReturnAllUsers() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "Артем Егоров", "Egorov@mail.ru"),
                new UserDto(2L, "Аня Иванова", "Anya@mail.ru")
        );

        when(userService.findAllUsers())
                .thenReturn(users);
        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Артем Егоров")))
                .andExpect(jsonPath("$[0].email", is("Egorov@mail.ru")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Аня Иванова")))
                .andExpect(jsonPath("$[1].email", is("Anya@mail.ru")));
    }

    @Test
    void mustReturnUserById() throws Exception {
        when(userService.findUserById(1L))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Макс Иванов")))
                .andExpect(jsonPath("$.email", is("Max@mail.ru")));
    }

    @Test
    void mustUpdateUser() throws Exception {
        UserDto updatedUser = new UserDto(1L, "Макс Иванов", "MaxIv@mail.ru");
        when(userService.updateUser(eq(1L), any(UserDto.class)))
                .thenReturn(updatedUser);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(updatedUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Макс Иванов")))
                .andExpect(jsonPath("$.email", is("MaxIv@mail.ru")));
    }

    @Test
    void mustDeleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }
}
