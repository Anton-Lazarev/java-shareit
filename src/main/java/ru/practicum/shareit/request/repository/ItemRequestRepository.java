package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    @Query(value = "select * from requests as r where r.requestor_id = ?1 order by r.created desc", nativeQuery = true)
    List<ItemRequest> findAllByUserID(int id);

    @Query(value = "select * from requests as r where r.requestor_id != ?1", nativeQuery = true)
    List<ItemRequest> findAllFromAnotherUsers(int userID, Pageable pageable);
}
