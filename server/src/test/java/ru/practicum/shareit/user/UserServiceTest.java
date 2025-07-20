package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
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
}
