package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("GET /users");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable("id") Long id) {
        log.info("GET /users/{}", id);
        return userService.findUserById(id);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("POST /users");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        log.info("PATCH /users/{}", id);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{}", id);
        userService.deleteUser(id);
    }
}
