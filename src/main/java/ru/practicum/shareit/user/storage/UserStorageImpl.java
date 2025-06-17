package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
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
        return users.get(id);
    }

    @Override
    public User createUser(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updateUser) {
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
