package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getAll();

    User addUser(User user);

    void updateUser(User user);

    void deleteUser(int id);

    User findUserByID(int id);

    boolean containsID(int id);

    boolean containsEmail(String email);
}
