package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

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
    public User createUser(User user) {
        if (userStorage.findAllUsers().stream()
                .anyMatch(viewedUser -> Objects.equals(viewedUser.getEmail(), user.getEmail()))) {
            log.warn("Данная электронная почта {} уже существует", user.getEmail());
            throw new InternalServerException("Данная электронная почта уже существует");
        }

        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User updateUser) {
        Optional<User> existingUserWithEmail = userStorage.findAllUsers().stream()
                .filter(viewedUser -> Objects.equals(viewedUser.getEmail(), updateUser.getEmail()) && !updateUser.getId().equals(viewedUser.getId()))
                .findAny();
        if (existingUserWithEmail.isPresent()) {
            log.warn("Данная электронная почта {} уже существует, и запрашиваемый id {} не найден", updateUser.getEmail(), updateUser.getId());
            throw new InternalServerException("Данная электронная почта уже существует и запрашиваемый id не найден");
        }

        User existingUser = userStorage.findUserById(updateUser.getId());
        if (existingUser == null) {
            log.warn("Пользователь с id: {} не найден", updateUser.getId());
            throw new NotFoundException("Пользователь с данным id " + updateUser.getId() + " не найден.");
        }

        return userStorage.updateUser(updateUser);
    }

    @Override
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }
}
