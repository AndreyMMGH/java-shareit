package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Data
@Slf4j
public class UserDto {
    private Long id;
    @NotNull(message = "Имя не может быть null")
    private String name;
    @NotBlank
    @NotNull(message = "Email не может быть null")
    @Email(message = "Недействительный адрес электронной почты")
    private String email;
}
