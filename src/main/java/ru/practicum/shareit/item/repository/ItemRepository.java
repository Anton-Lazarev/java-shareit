package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("select it from Item as it where (lower(it.name) like concat('%', ?1, '%') " +
            "or lower(it.description) like concat('%', ?1, '%')) and it.available = true")
    List<Item> findByNameOrDescriptionContainingIgnoreCase(String text);

    @Query(value = "select * from items as i where i.owner_id = ?1", nativeQuery = true)
    List<Item> findAllByUserId(int id);
}
