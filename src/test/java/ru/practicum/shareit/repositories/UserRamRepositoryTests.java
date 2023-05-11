package ru.practicum.shareit.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRamRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRamRepositoryTests {
    UserRamRepository repository;
    User firstUser;
    User secondUser;

    @BeforeEach
    void beforeEach() {
        repository = new UserRamRepository();
        firstUser = User.builder().name("First").email("first@email.com").build();
        secondUser = User.builder().name("Second").email("second@email.com").build();
    }

    @Test
    void emptyUsersAfterStartStorage() {
        assertEquals(0, repository.getAll().size());
    }

    @Test
    void correctUserIdInAddingIfIdPresent() {
        firstUser.setId(54);
        repository.addUser(firstUser);
        ArrayList<User> users = new ArrayList<>(repository.getAll());
        assertEquals(1, users.get(0).getId());
    }

    @Test
    void correctAddUser() {
        repository.addUser(secondUser);
        ArrayList<User> users = new ArrayList<>(repository.getAll());
        assertEquals(1, users.get(0).getId());
        assertEquals("Second", users.get(0).getName());
        assertEquals("second@email.com", users.get(0).getEmail());
    }

    @Test
    void correctAddFewUsers() {
        repository.addUser(firstUser);
        repository.addUser(secondUser);
        ArrayList<User> users = new ArrayList<>(repository.getAll());
        assertEquals(2, users.size());

        assertEquals(1, users.get(0).getId());
        assertEquals("First", users.get(0).getName());
        assertEquals("first@email.com", users.get(0).getEmail());

        assertEquals(2, users.get(1).getId());
        assertEquals("Second", users.get(1).getName());
        assertEquals("second@email.com", users.get(1).getEmail());
    }

    @Test
    void correctUpdateUser() {
        repository.addUser(firstUser);
        User update = User.builder().id(1).name("update").email("update@email.com").build();
        repository.updateUser(update);

        ArrayList<User> users = new ArrayList<>(repository.getAll());
        assertEquals(1, users.get(0).getId());
        assertEquals("update", users.get(0).getName());
        assertEquals("update@email.com", users.get(0).getEmail());
    }

    @Test
    void deleteOldMailAfterUpdating() {
        repository.addUser(firstUser);
        User update = User.builder().id(1).name("update").email("update@email.com").build();
        repository.updateUser(update);
        assertFalse(repository.containsEmail("first@email.com"));
    }

    @Test
    void correctGettingUserByID() {
        repository.addUser(secondUser);
        User user = repository.findUserByID(1);
        assertEquals("Second", user.getName());
        assertEquals("second@email.com", user.getEmail());
    }

    @Test
    void correctCheckEmail() {
        repository.addUser(firstUser);
        assertTrue(repository.containsEmail("first@email.com"));
    }

    @Test
    void returnFalseWhenEmailNotPresent() {
        repository.addUser(secondUser);
        assertFalse(repository.containsEmail("first@email.com"));
    }

    @Test
    void returnTrueWhenIdInBase() {
        repository.addUser(firstUser);
        repository.addUser(secondUser);
        assertTrue(repository.containsID(2));
    }

    @Test
    void returnFalseWhenIdNotPresent() {
        repository.addUser(firstUser);
        repository.addUser(secondUser);
        assertFalse(repository.containsID(585));
    }

    @Test
    void correctDeletingUser() {
        repository.addUser(firstUser);
        repository.addUser(secondUser);
        repository.deleteUser(1);
        assertEquals(1, repository.getAll().size());
        assertFalse(repository.containsEmail("first@email.com"));
        assertEquals("Second", repository.findUserByID(2).getName());
    }
}
