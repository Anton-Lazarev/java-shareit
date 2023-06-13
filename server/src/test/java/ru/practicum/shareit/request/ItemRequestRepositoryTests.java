package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTests {
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    void findAllByUserID_correctListWhenHaveAnotherUsers() {
        User user = userRepository.save(User.builder().name("f").email("f@f.f").build());
        User anotherUser = userRepository.save(User.builder().name("r").email("r@r.r").build());
        ItemRequest firstRequest = requestRepository.save(ItemRequest.builder()
                .requestor(user)
                .created(LocalDateTime.now())
                .description("Want a drill")
                .build());
        ItemRequest secondRequest = requestRepository.save(ItemRequest.builder()
                .requestor(anotherUser)
                .created(LocalDateTime.now().plusHours(1))
                .description("Want a water")
                .build());
        ItemRequest thirdRequest = requestRepository.save(ItemRequest.builder()
                .requestor(user)
                .created(LocalDateTime.now().plusHours(2))
                .description("Want a cucumber")
                .build());

        List<ItemRequest> requests = requestRepository.findAllByUserID(user.getId());

        assertEquals(2, requests.size());
        assertEquals(thirdRequest.getId(), requests.get(0).getId());
        assertEquals(thirdRequest.getRequestor(), requests.get(0).getRequestor());
        assertEquals(thirdRequest.getDescription(), requests.get(0).getDescription());
        assertEquals(thirdRequest.getCreated(), requests.get(0).getCreated());
        assertEquals(firstRequest.getId(), requests.get(1).getId());
        assertEquals(firstRequest.getRequestor(), requests.get(1).getRequestor());
        assertEquals(firstRequest.getDescription(), requests.get(1).getDescription());
        assertEquals(firstRequest.getCreated(), requests.get(1).getCreated());
    }

    @Test
    void findAllByUserID_emptyListWhenNothingFounded() {
        User user = userRepository.save(User.builder().name("f").email("f@f.f").build());
        User anotherUser = userRepository.save(User.builder().name("r").email("r@r.r").build());
        requestRepository.save(ItemRequest.builder()
                .requestor(user)
                .created(LocalDateTime.now())
                .description("Want a drill")
                .build());
        requestRepository.save(ItemRequest.builder()
                .requestor(user)
                .created(LocalDateTime.now().plusHours(2))
                .description("Want a cucumber")
                .build());

        List<ItemRequest> requests = requestRepository.findAllByUserID(anotherUser.getId());

        assertEquals(0, requests.size());
    }

    @Test
    void findAllFromAnotherUsers_correctSearch() {
        User user = userRepository.save(User.builder().name("f").email("f@f.f").build());
        User anotherUser = userRepository.save(User.builder().name("r").email("r@r.r").build());
        ItemRequest firstRequest = requestRepository.save(ItemRequest.builder()
                .requestor(anotherUser)
                .created(LocalDateTime.now())
                .description("Want a drill")
                .build());
        ItemRequest secondRequest = requestRepository.save(ItemRequest.builder()
                .requestor(user)
                .created(LocalDateTime.now().plusHours(1))
                .description("Want a water")
                .build());
        ItemRequest thirdRequest = requestRepository.save(ItemRequest.builder()
                .requestor(anotherUser)
                .created(LocalDateTime.now().plusHours(2))
                .description("Want a cucumber")
                .build());

        List<ItemRequest> requests = requestRepository.findAllFromAnotherUsers(anotherUser.getId(), Pageable.ofSize(5));

        assertEquals(1, requests.size());
        assertEquals(secondRequest.getId(), requests.get(0).getId());
        assertEquals(secondRequest.getRequestor(), requests.get(0).getRequestor());
        assertEquals(secondRequest.getDescription(), requests.get(0).getDescription());
        assertEquals(secondRequest.getCreated(), requests.get(0).getCreated());
    }

    @Test
    void findAllFromAnotherUsers_emptyList_whenNothingFounded() {
        User user = userRepository.save(User.builder().name("f").email("f@f.f").build());
        User anotherUser = userRepository.save(User.builder().name("r").email("r@r.r").build());
        requestRepository.save(ItemRequest.builder()
                .requestor(anotherUser)
                .created(LocalDateTime.now())
                .description("Want a drill")
                .build());
        requestRepository.save(ItemRequest.builder()
                .requestor(anotherUser)
                .created(LocalDateTime.now().plusHours(1))
                .description("Want a water")
                .build());
        requestRepository.save(ItemRequest.builder()
                .requestor(anotherUser)
                .created(LocalDateTime.now().plusHours(2))
                .description("Want a cucumber")
                .build());

        List<ItemRequest> requests = requestRepository.findAllFromAnotherUsers(anotherUser.getId(), Pageable.ofSize(5));

        assertEquals(0, requests.size());
    }
}
