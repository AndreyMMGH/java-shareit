package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentRequestDto {
    @Size(max = 2000, message = "Комментарий не может быть длиннее 2000 символов")
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
}
