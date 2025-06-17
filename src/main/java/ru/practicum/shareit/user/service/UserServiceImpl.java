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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @Override
    public User findUserById(Long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            log.warn("Пользователь с данным id {} не найден", id);
            throw new NotFoundException("Пользователь с данным id: " + id + " не найден");
        }
        return user;
    }

    @Override
    public User createUser(UserDto userDto) {
        if (userStorage.findAllUsers().stream()
                .anyMatch(viewedUser -> Objects.equals(viewedUser.getEmail(), userDto.getEmail()))) {
            log.warn("Данная электронная почта {} уже существует", userDto.getEmail());
            throw new InternalServerException("Данная электронная почта уже существует");
        }

        return userStorage.createUser(UserMapper.toUser(userDto));
    }

    @Override
    public User updateUser(Long id, UserDto updateUserDto) {
        User existingUser = userStorage.findUserById(id);
        if (existingUser == null) {
            log.warn("Пользователь с id: {} не найден", id);
            throw new NotFoundException("Пользователь с данным id " + id + " не найден.");
        }

        Optional<User> existingUserWithEmail = userStorage.findAllUsers().stream()
                .filter(viewedUser -> Objects.equals(viewedUser.getEmail(), updateUserDto.getEmail()) && !Objects.equals(id, viewedUser.getId()))
                .findAny();
        if (existingUserWithEmail.isPresent()) {
            log.warn("Пользователь с таким email уже существует");
            throw new InternalServerException("Пользователь с таким email уже существует");
        }

        return userStorage.updateUser(UserMapper.toUser(id, updateUserDto));
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }
}
