package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EmailAlreadyExistException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository repository;
    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getAllUsers_emptyList_whenEmptyUsers() {
        Mockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        List<UserDTO> dtos = service.getAllUsers();

        verify(repository, atMostOnce()).findAll();
        assertEquals(0, dtos.size());
    }

    @Test
    void getAllUsers_listWithTwoUsers() {
        ArrayList<User> answer = new ArrayList<>();
        answer.add(User.builder().id(1).name("one").email("one@one.ne").build());
        answer.add(User.builder().id(2).name("two").email("two@two.ne").build());

        Mockito.doReturn(answer).when(repository).findAll();
        List<UserDTO> dtos = service.getAllUsers();

        verify(repository, atMostOnce()).findAll();
        assertEquals(2, dtos.size());
        assertEquals(1, dtos.get(0).getId());
        assertEquals(2, dtos.get(1).getId());
    }

    @Test
    void addUser_correctAdding() {
        UserDTO dtoForSave = UserDTO.builder().name("exam").email("exa@m.ru").build();
        User user = User.builder().name("exam").email("exa@m.ru").build();

        when(repository.save(any())).thenReturn(User.builder().id(1).name("exam").email("exa@m.ru").build());
        UserDTO savedUser = service.addUser(dtoForSave);

        verify(repository, atMostOnce()).save(user);
        assertEquals("exam", savedUser.getName());
        assertEquals("exa@m.ru", savedUser.getEmail());
        assertEquals(1, savedUser.getId());
    }

    @Test
    void patchUser_exception_whenUserNotPresent() {
        UserDTO dtoForSave = UserDTO.builder().id(5).name("exam").email("exa@m.ru").build();

        when(repository.existsById(dtoForSave.getId())).thenReturn(false);

        verify(repository, never()).save(UserMapper.userDtoToUser(dtoForSave));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.patchUser(dtoForSave));
        assertEquals("User with ID 5 not present", exception.getMessage());
    }

    @Test
    void patchUser_exception_whenEmailAlreadyPresent() {
        UserDTO dtoForSave = UserDTO.builder().id(5).name("exam").email("exa@m.ru").build();
        ArrayList<User> userWithSameMail = new ArrayList<>();
        userWithSameMail.add(User.builder().id(7).name("ram").email("exa@m.ru").build());
        User old = User.builder().id(5).name("pipo").name("pi@po.po").build();

        when(repository.existsById(dtoForSave.getId())).thenReturn(true);
        doReturn(Optional.of(old)).when(repository).findById(dtoForSave.getId());
        doReturn(userWithSameMail).when(repository).findByEmailIgnoreCase(dtoForSave.getEmail());

        verify(repository, never()).save(UserMapper.userDtoToUser(dtoForSave));
        EmailAlreadyExistException exception = assertThrows(EmailAlreadyExistException.class,
                () -> service.patchUser(dtoForSave));
        assertEquals("Email exa@m.ru already exist in base, can't patch user", exception.getMessage());
    }

    @Test
    void patchUser_correctPatchOnlyWithName() {
        UserDTO dtoForPatch = UserDTO.builder().id(6).name("patch").build();
        User user = User.builder().id(6).name("exam").email("exa@m.ru").build();
        User patchedUser = User.builder().id(6).name("patch").email("exa@m.ru").build();

        when(repository.existsById(dtoForPatch.getId())).thenReturn(true);
        doReturn(Optional.of(user)).when(repository).findById(dtoForPatch.getId());
        doReturn(patchedUser).when(repository).save(any());

        UserDTO dto = service.patchUser(dtoForPatch);
        verify(repository, atMostOnce()).existsById(dtoForPatch.getId());
        verify(repository, atMostOnce()).save(user);
        assertEquals(6, dto.getId());
        assertEquals("patch", dto.getName());
        assertEquals("exa@m.ru", dto.getEmail());
    }

    @Test
    void patchUser_correctPatchOnlyWithEmail() {
        UserDTO dtoForPatch = UserDTO.builder().id(3).email("patch@ap.com").build();
        User user = User.builder().id(3).name("exam").email("exa@m.ru").build();
        User patchedUser = User.builder().id(3).name("exam").email("patch@ap.com").build();

        when(repository.existsById(dtoForPatch.getId())).thenReturn(true);
        doReturn(Optional.of(user)).when(repository).findById(dtoForPatch.getId());
        doReturn(patchedUser).when(repository).save(any());

        UserDTO dto = service.patchUser(dtoForPatch);
        verify(repository, atMostOnce()).existsById(dtoForPatch.getId());
        verify(repository, atMostOnce()).save(user);
        assertEquals(3, dto.getId());
        assertEquals("exam", dto.getName());
        assertEquals("patch@ap.com", dto.getEmail());
    }

    @Test
    void patchUser_correctPatch() {

    }

    @Test
    void deleteUser_exception_whenUserNotPresent() {
        int id = 10;
        when(repository.existsById(id)).thenReturn(false);

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.deleteUser(id));
        verify(repository, never()).deleteById(id);
        assertEquals("User with ID 10 not present", exception.getMessage());
    }

    @Test
    void deleteUser_correctDeletion() {
        int id = 10;
        verify(repository, atMostOnce()).deleteById(id);
    }

    @Test
    void getUserByID_exception_whenUserNotPresent() {
        int id = 36;
        when(repository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> service.getUserByID(id));
        verify(repository, atMostOnce()).findById(id);
        assertEquals("User with ID 36 not present", exception.getMessage());
    }

    @Test
    void getUserByID_correctGettingUser() {
        int id = 3;
        User user = User.builder().id(3).name("third").email("thi@rd.rd").build();

        when(repository.findById(id)).thenReturn(Optional.of(user));

        UserDTO dto = service.getUserByID(id);
        verify(repository, atMostOnce()).findById(id);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }
}
