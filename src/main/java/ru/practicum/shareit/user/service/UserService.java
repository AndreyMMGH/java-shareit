package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    User findUserById(Long id);

    User createUser(UserDto userDto);

    User updateUser(Long id, UserDto updateUserDto);

    void deleteUser(Long id);
}
