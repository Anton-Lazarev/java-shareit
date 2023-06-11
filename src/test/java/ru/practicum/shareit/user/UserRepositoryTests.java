package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository repository;

    @BeforeEach
    void addUsers() {
        repository.save(User.builder().name("first").email("first@mail.ru").build());
        repository.save(User.builder().name("second").email("second@mail.ru").build());
        repository.save(User.builder().name("third").email("third@mail.com").build());
    }

    @Test
    void findByEmail_emptyWhenNothingFounded() {
        List<User> users = repository.findByEmailIgnoreCase("mars");

        assertEquals(0, users.size());
    }

    @Test
    void findByEmail_oneMatchFounded() {
        List<User> users = repository.findByEmailIgnoreCase("first@mail.ru");

        assertEquals(1, users.size());
        assertEquals("first", users.get(0).getName());
        assertEquals("first@mail.ru", users.get(0).getEmail());
    }

    @Test
    void findByEmail_oneMatchFounded_whenCaseInvalid() {
        List<User> users = repository.findByEmailIgnoreCase("tHiRd@MAIL.com");

        assertEquals(1, users.size());
        assertEquals("third", users.get(0).getName());
        assertEquals("third@mail.com", users.get(0).getEmail());
    }

    @AfterEach
    void clearUsers() {
        repository.deleteAll();
    }
}
