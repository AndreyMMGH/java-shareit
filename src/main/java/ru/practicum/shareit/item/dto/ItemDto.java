package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя не может быть null")
    private String name;
    @NotBlank(message = "Описание не может быть null")
    private String description;
    @NotNull(message = "Поле доступ не может быть null")
    private Boolean available;
}
