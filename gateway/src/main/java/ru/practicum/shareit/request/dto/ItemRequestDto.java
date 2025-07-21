package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ItemRequestDto {
    @Size(max = 2000, message = "Описание не может быть длиннее 2000 символов")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "UserId не может быть null")
    private Long requestorId;
}
