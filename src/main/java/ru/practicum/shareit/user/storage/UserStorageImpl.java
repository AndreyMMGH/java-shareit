package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Slf4j
@Repository
public class UserStorageImpl implements UserStorage {
    private Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.warn("Пользователь с данным id {} не найден", id);
            throw new NotFoundException("Пользователь с данным id: " + id + " не найден");
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        if (users.values().stream()
                .anyMatch(viewedUser -> Objects.equals(viewedUser.getEmail(), user.getEmail()))) {
            log.warn("Данная электронная почта {} уже существует", user.getEmail());
            throw new InternalServerException("Данная электронная почта уже существует");
        }

        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updateUser) {
        if (users.values().stream()
                .anyMatch(viewedUser -> Objects.equals(viewedUser.getEmail(), updateUser.getEmail()) && !updateUser.getId().equals(viewedUser.getId()))) {
            log.warn("Данная электронная почта {} уже существует, и запрашиваемый id {} не найден", updateUser.getEmail(), updateUser.getId());
            throw new InternalServerException("Данная электронная почта уже существует и запрашиваемый id не найден");
        }

        User existingUser = users.get(updateUser.getId());
        if (existingUser == null) {
            log.warn("Пользователь с id: {} не найден", updateUser.getId());
            throw new NotFoundException("Пользователь с данным id " + updateUser.getId() + " не найден.");
        }

        users.remove(existingUser.getId());
        users.put(updateUser.getId(), updateUser);
        return updateUser;
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    private Long getId() {
        long lastId = users.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
