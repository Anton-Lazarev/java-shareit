package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findItemByNameAndDesc_emptyList_whenNothingFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).build());

        List<Item> items = itemRepository.findItemByNameAndDesc("rub", PageRequest.of(0, 5));

        assertEquals(0, items.size());
    }

    @Test
    void findItemByNameAndDesc_oneFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).build());

        List<Item> items = itemRepository.findItemByNameAndDesc("euro", PageRequest.of(0, 5));

        assertEquals(1, items.size());
        assertEquals(secondItem.getName(), items.get(0).getName());
        assertEquals(secondItem.getDescription(), items.get(0).getDescription());
    }

    @Test
    void findItemByNameAndDesc_twoFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).build());

        List<Item> items = itemRepository.findItemByNameAndDesc("one", PageRequest.of(0, 5));

        assertEquals(2, items.size());
        assertEquals(firstItem.getName(), items.get(0).getName());
        assertEquals(firstItem.getDescription(), items.get(0).getDescription());
        assertEquals(secondItem.getName(), items.get(1).getName());
        assertEquals(secondItem.getDescription(), items.get(1).getDescription());
    }

    @Test
    void findItemByNameAndDesc_twoFounded_firstPage() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).build());

        List<Item> items = itemRepository.findItemByNameAndDesc("one", PageRequest.of(0, 1));

        assertEquals(1, items.size());
        assertEquals(firstItem.getName(), items.get(0).getName());
        assertEquals(firstItem.getDescription(), items.get(0).getDescription());
    }

    @Test
    void findItemByNameAndDesc_twoFounded_secondPage() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).build());

        List<Item> items = itemRepository.findItemByNameAndDesc("one", PageRequest.of(1, 1));

        assertEquals(1, items.size());
        assertEquals(secondItem.getName(), items.get(0).getName());
        assertEquals(secondItem.getDescription(), items.get(0).getDescription());
    }

    @Test
    void findAllByUserId_emptyList_whenNothingFounded() {
        User firstOwner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User secondOwner = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(secondOwner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(secondOwner).build());

        List<Item> items = itemRepository.findAllByUserId(firstOwner.getId(), PageRequest.of(0, 5));

        assertEquals(0, items.size());
    }

    @Test
    void findAllByUserId_oneFounded() {
        User firstOwner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User secondOwner = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(secondOwner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(firstOwner).build());

        List<Item> items = itemRepository.findAllByUserId(firstOwner.getId(), PageRequest.of(0, 5));

        assertEquals(1, items.size());
        assertEquals(secondItem.getName(), items.get(0).getName());
        assertEquals(secondItem.getDescription(), items.get(0).getDescription());
    }

    @Test
    void findAllByUserId_twoFounded() {
        User firstOwner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(firstOwner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(firstOwner).build());

        List<Item> items = itemRepository.findAllByUserId(firstOwner.getId(), PageRequest.of(0, 5));

        assertEquals(2, items.size());
        assertEquals(firstItem.getName(), items.get(0).getName());
        assertEquals(firstItem.getDescription(), items.get(0).getDescription());
        assertEquals(secondItem.getName(), items.get(1).getName());
        assertEquals(secondItem.getDescription(), items.get(1).getDescription());
    }

    @Test
    void findAllByUserId_twoFounded_firstPage() {
        User firstOwner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(firstOwner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(firstOwner).build());

        List<Item> items = itemRepository.findAllByUserId(firstOwner.getId(), PageRequest.of(0, 1));

        assertEquals(1, items.size());
        assertEquals(firstItem.getName(), items.get(0).getName());
        assertEquals(firstItem.getDescription(), items.get(0).getDescription());
    }

    @Test
    void findAllByUserId_twoFounded_secondPage() {
        User firstOwner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(firstOwner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(firstOwner).build());

        List<Item> items = itemRepository.findAllByUserId(firstOwner.getId(), PageRequest.of(1, 1));

        assertEquals(1, items.size());
        assertEquals(secondItem.getName(), items.get(0).getName());
        assertEquals(secondItem.getDescription(), items.get(0).getDescription());
    }

    @Test
    void findAllByRequestID_emptyList_whenNothingFounded() {
        User requestor = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        ItemRequest request = requestRepository.save(ItemRequest.builder().requestor(requestor).description("need money").created(LocalDateTime.now().minusDays(2)).build());
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).build());

        List<Item> items = itemRepository.findAllByRequestID(request.getId());

        assertEquals(0, items.size());
    }

    @Test
    void findAllByRequestID_oneFounded() {
        User requestor = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        ItemRequest request = requestRepository.save(ItemRequest.builder().requestor(requestor).description("need money").created(LocalDateTime.now().minusDays(2)).build());
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).request(request).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).build());

        List<Item> items = itemRepository.findAllByRequestID(request.getId());

        assertEquals(1, items.size());
        assertEquals(firstItem.getName(), items.get(0).getName());
        assertEquals(firstItem.getDescription(), items.get(0).getDescription());
        assertEquals(firstItem.getRequest().getId(), items.get(0).getRequest().getId());
    }

    @Test
    void findAllByRequestID_twoFounded() {
        User requestor = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        ItemRequest request = requestRepository.save(ItemRequest.builder().requestor(requestor).description("need money").created(LocalDateTime.now().minusDays(2)).build());
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).request(request).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).request(request).build());

        List<Item> items = itemRepository.findAllByRequestID(request.getId());

        assertEquals(2, items.size());
        assertEquals(firstItem.getName(), items.get(0).getName());
        assertEquals(firstItem.getDescription(), items.get(0).getDescription());
        assertEquals(firstItem.getRequest().getId(), items.get(0).getRequest().getId());
        assertEquals(secondItem.getName(), items.get(1).getName());
        assertEquals(secondItem.getDescription(), items.get(1).getDescription());
        assertEquals(secondItem.getRequest().getId(), items.get(1).getRequest().getId());
    }
}
