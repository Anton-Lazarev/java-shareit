package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("select it from Item as it where (lower(it.name) like concat('%', :text, '%') " +
            "or lower(it.description) like concat('%', :text, '%')) and it.available = true")
    List<Item> findItemByNameAndDesc(@Param("text") String text, Pageable pageable);

    @Query(value = "select * from items as i where i.owner_id = :id order by id", nativeQuery = true)
    List<Item> findAllByUserId(@Param("id") int id, Pageable pageable);

    @Query(value = "select * from items as i where i.request_id = :id", nativeQuery = true)
    List<Item> findAllByRequestID(@Param("id") int id);
}
