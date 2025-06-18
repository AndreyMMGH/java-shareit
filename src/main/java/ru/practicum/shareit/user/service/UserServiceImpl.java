package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<UserDto> findAllUsers() {
        return userStorage.findAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = userStorage.findUserById(id);

        if (user == null) {
            log.warn("Пользователь с данным id {} не найден", id);
            throw new NotFoundException("Пользователь с данным id: " + id + " не найден");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        ValidateEmail(userDto);

        return UserMapper.toUserDto(userStorage.createUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(Long id, UserDto updateUserDto) {
        User existingUser = userStorage.findUserById(id);

        if (existingUser == null) {
            log.warn("Пользователь с id: {} не найден", id);
            throw new NotFoundException("Пользователь с данным id " + id + " не найден.");
        }

        ValidateEmail(updateUserDto);

        return UserMapper.toUserDto(userStorage.updateUser(UserMapper.toUser(id, updateUserDto)));
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public void ValidateEmail(UserDto userDto) {
        if (userStorage.findAllUsers().stream()
                .anyMatch(viewedUser -> Objects.equals(viewedUser.getEmail(), userDto.getEmail()))) {
            log.warn("Данная электронная почта {} уже существует", userDto.getEmail());
            throw new InternalServerException("Данная электронная почта уже существует");
        }
    }
}
