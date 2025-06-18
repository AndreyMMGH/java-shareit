package ru.practicum.shareit.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class ErrorResponse {
    private final String error;
    private final String description;
}
