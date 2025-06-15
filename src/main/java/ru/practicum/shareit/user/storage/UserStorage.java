package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {

    List<User> findAllUsers();

    User findUserById(Long id);

    User createUser(User user);
}
