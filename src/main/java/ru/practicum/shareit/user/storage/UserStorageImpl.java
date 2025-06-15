package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class UserStorageImpl implements UserStorage {
    //private final List<User> users = new ArrayList<>();
    private Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(Long id) {
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        user.setId(getId());
        users.put(getId(), user);
        return user;
    }

    private Long getId() {
        long lastId = users.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
