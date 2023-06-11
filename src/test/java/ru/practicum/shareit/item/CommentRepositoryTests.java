package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findAllByItemID_emptyList_whenNothingFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User author = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).build());
        Comment comment = commentRepository.save(Comment.builder().item(firstItem).text("cool dollar").author(author).created(LocalDateTime.now().minusDays(2)).build());

        List<Comment> comments = commentRepository.findAllByItemID(secondItem.getId());

        assertEquals(0, comments.size());
    }

    @Test
    void findAllByItemID_oneFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User author = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).build());
        Comment firstComment = commentRepository.save(Comment.builder().item(firstItem).text("cool dollar").author(author).created(LocalDateTime.now().minusDays(2)).build());
        Comment secondComment = commentRepository.save(Comment.builder().item(secondItem).text("cool euro").author(author).created(LocalDateTime.now().minusDays(1)).build());

        List<Comment> comments = commentRepository.findAllByItemID(firstItem.getId());

        assertEquals(1, comments.size());
        assertEquals(firstComment.getText(), comments.get(0).getText());
        assertEquals(firstComment.getAuthor().getName(), comments.get(0).getAuthor().getName());
        assertEquals(firstComment.getItem().getName(), comments.get(0).getItem().getName());
        assertEquals(firstComment.getCreated(), comments.get(0).getCreated());
    }

    @Test
    void findAllByItemID_twoFounded() {
        User owner = userRepository.save(User.builder().name("Jo").email("j@i.jo").build());
        User author = userRepository.save(User.builder().name("Leo").email("l@e.o").build());
        Item firstItem = itemRepository.save(Item.builder().name("dollar").description("one dollar").available(true).owner(owner).build());
        Item secondItem = itemRepository.save(Item.builder().name("euro").description("one euro").available(true).owner(owner).build());
        Comment firstComment = commentRepository.save(Comment.builder().item(secondItem).text("cool dollar").author(author).created(LocalDateTime.now().minusDays(2)).build());
        Comment secondComment = commentRepository.save(Comment.builder().item(secondItem).text("cool euro").author(author).created(LocalDateTime.now().minusDays(1)).build());

        List<Comment> comments = commentRepository.findAllByItemID(secondItem.getId());

        assertEquals(2, comments.size());
        assertEquals(secondComment.getText(), comments.get(0).getText());
        assertEquals(secondComment.getAuthor().getName(), comments.get(0).getAuthor().getName());
        assertEquals(secondComment.getItem().getName(), comments.get(0).getItem().getName());
        assertEquals(secondComment.getCreated(), comments.get(0).getCreated());
        assertEquals(firstComment.getText(), comments.get(1).getText());
        assertEquals(firstComment.getAuthor().getName(), comments.get(1).getAuthor().getName());
        assertEquals(firstComment.getItem().getName(), comments.get(1).getItem().getName());
        assertEquals(firstComment.getCreated(), comments.get(1).getCreated());
    }
}
