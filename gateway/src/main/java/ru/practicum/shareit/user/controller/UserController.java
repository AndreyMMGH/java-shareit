package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.client.UserClient;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("GET /users");
        return userClient.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable("id") Long id) {
        log.info("GET /users/{}", id);
        return userClient.findUserById(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST /users");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        log.info("PATCH /users/{}", id);
        return userClient.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{}", id);
        return userClient.deleteUser(id);
    }
}
