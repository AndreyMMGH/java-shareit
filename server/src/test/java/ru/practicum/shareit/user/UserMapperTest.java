package ru.practicum.shareit.user;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class UserMapperTest {
    @Test
    void shouldMapUserDtoToUser() {
        UserDto dto = new UserDto(1L, "Макс Иванов", "max@mail.ru");

        User result = UserMapper.toUser(dto);

        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(1L);
        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("Макс Иванов");
        AssertionsForClassTypes.assertThat(result.getEmail()).isEqualTo("max@mail.ru");
    }
}
