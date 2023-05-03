package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component("UserRamStorage")
public class UserRamStorage implements UserStorage {
    private int nextID = 1;
    private Map<Integer, User> users = new HashMap<>();
    private Set<String> mails = new HashSet<>();


    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(nextID);
        nextID++;
        users.put(user.getId(), user);
        mails.add(user.getEmail());
        return user;
    }

    @Override
    public void updateUser(User user) {
        mails.remove(users.get(user.getId()).getEmail());
        mails.add(user.getEmail());
        users.put(user.getId(), user);
    }

    @Override
    public void deleteUser(int id) {
        mails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public User findUserByID(int id) {
        return users.get(id);
    }

    @Override
    public boolean containsID(int id) {
        return users.containsKey(id);
    }

    @Override
    public boolean containsEmail(String email) {
        return mails.contains(email);
    }
}
