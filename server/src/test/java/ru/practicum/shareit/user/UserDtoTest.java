package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ContextConfiguration(classes = ShareItApp.class)
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoTest {
    private final JacksonTester<UserDto> jsonUserDto;

    @Test
    void shouldReturnUserDto() throws Exception
    {
        UserDto userDto = new UserDto(
                1L,
                "Макс Иванов",
                "Max@mail.ru"
        );

        JsonContent<UserDto> result = jsonUserDto.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Макс Иванов");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Max@mail.ru");
    }

    @Test
    void shouldDeserializeUserDtoFromJson() throws Exception
    {
        String json = """
                {
                  "id": 1,
                  "name": "Макс Иванов",
                  "email": "Max@mail.ru"
                }
                """;

        UserDto result = jsonUserDto.parse(json).getObject();

        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(1L);
        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("Макс Иванов");
        AssertionsForClassTypes.assertThat(result.getEmail()).isEqualTo("Max@mail.ru");
    }
}
