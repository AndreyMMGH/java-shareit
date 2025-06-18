package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> findAllUsers();

    User findUserById(Long id);

    User createUser(User user);

    User updateUser(User updateUser);

    void deleteUser(Long id);
}
