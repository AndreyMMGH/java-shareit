package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(
        classes = ShareItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final EntityManager em;
    private final UserService userService;
    private Long userId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Макс Иванов");
        user.setEmail("Max@mail.ru");

        em.persist(user);
        em.flush();

        userId = user.getId();
    }

    @Test
    void mustReturnUserById() {
        UserDto itemResponseDto = userService.findUserById(userId);

        assertThat(itemResponseDto.getId()).isNotNull();
        assertThat(itemResponseDto.getId()).isEqualTo(userId);
        assertThat(itemResponseDto.getName()).isEqualTo("Макс Иванов");
        assertThat(itemResponseDto.getEmail()).isEqualTo("Max@mail.ru");
    }


    @Test
    void shouldReturnException() {
        Assertions.assertThrows(NotFoundException.class, () -> userService.findUserById(999999L));
    }

    @Test
    void mustUpdateName() {
        UserDto updateDto = new UserDto();
        updateDto.setName("Макс Алексеев");
        updateDto.setEmail(null);

        UserDto updated = userService.updateUser(userId, updateDto);

        assertThat(updated.getName()).isEqualTo("Макс Алексеев");
        assertThat(updated.getEmail()).isEqualTo("Max@mail.ru");
    }

    @Test
    void mustUpdateEmail() {
        UserDto updateDto = new UserDto();
        updateDto.setName(null);
        updateDto.setEmail("newMax@mail.ru");

        UserDto updated = userService.updateUser(userId, updateDto);

        assertThat(updated.getEmail()).isEqualTo("newMax@mail.ru");
        assertThat(updated.getName()).isEqualTo("Макс Иванов");
    }

    @Test
    void emptyFieldsShouldIgnoredWhenUpdating() {
        UserDto updateDto = new UserDto();
        updateDto.setName("   ");
        updateDto.setEmail("   ");

        UserDto updated = userService.updateUser(userId, updateDto);

        assertThat(updated.getName()).isEqualTo("Макс Иванов");
        assertThat(updated.getEmail()).isEqualTo("Max@mail.ru");
    }

    @Test
    void shouldSuccessfullyCreateTheUser() {
        UserDto newUser = new UserDto();
        newUser.setName("Иван Петров");
        newUser.setEmail("ivan.petrov@mail.ru");

        UserDto created = userService.createUser(newUser);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Иван Петров");
        assertThat(created.getEmail()).isEqualTo("ivan.petrov@mail.ru");
    }

    @Test
    void exceptionMustBeThrown() {
        UserDto newUser = new UserDto();
        newUser.setName("Макс Егоров");
        newUser.setEmail("Max@mail.ru");

        Assertions.assertThrows(InternalServerException.class, () -> userService.createUser(newUser));
    }

    @Test
    void deleteUserExistingIdSuccess() {
        Assertions.assertDoesNotThrow(() -> userService.deleteUser(userId));
    }

    @Test
    void shouldReturnExceptionOnDelete() {
        Assertions.assertDoesNotThrow(() -> userService.deleteUser(999999L));
    }

    @Test
    void shouldReturnAllUsers() {
        var allUsers = userService.findAllUsers();

        assertThat(allUsers).isNotEmpty();
        assertThat(allUsers.stream().anyMatch(u -> u.getId().equals(userId))).isTrue();
    }
}
