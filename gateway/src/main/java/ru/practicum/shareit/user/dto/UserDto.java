package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть null")
    private String name;
    @NotBlank(message = "Email не может быть null")
    @Email(message = "Недействительный адрес электронной почты")
    private String email;
}
