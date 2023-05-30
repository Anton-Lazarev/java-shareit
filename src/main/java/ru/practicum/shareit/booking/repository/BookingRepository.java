package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query(value = "select * from bookings where booker_id = ?1 order by start_date desc", nativeQuery = true)
    List<Booking> findBookingsOfUserInStateALL(int userID);

    @Query(value = "select * from bookings where booker_id = ?1 and (status = 'REJECTED' or status = 'CANCELED') " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findBookingsOfUserInStateREJECTED(int userID);

    @Query(value = "select * from bookings where booker_id = ?1 and end_date < ?2 " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findBookingsOfUserInStatePAST(int userID, LocalDateTime dateTime);

    @Query(value = "select * from bookings where booker_id = ?1 and start_date > ?2 " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findBookingsOfUserInStateFUTURE(int userID, LocalDateTime dateTime);

    @Query(value = "select * from bookings where booker_id = ?1 " +
            "and (start_date < ?2 and end_date > ?2) " +
            "order by id", nativeQuery = true)
    List<Booking> findBookingsOfUserInStateCURRENT(int userID, LocalDateTime dateTime);

    @Query(value = "select * from bookings where booker_id = ?1 and status = 'WAITING' " +
            "and (start_date < ?2 or end_date > ?2) " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findBookingsOfUserInStateWAITING(int userID, LocalDateTime dateTime);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 order by b.start desc")
    List<Booking> findBookingsOfItemOwnerInStateALL(int userID);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 " +
            "and (b.start < ?2 and b.end > ?2) order by b.id")
    List<Booking> findBookingsOfItemOwnerInStateCURRENT(int userID, LocalDateTime dateTime);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findBookingsOfItemOwnerInStatePAST(int userID, LocalDateTime dateTime);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 and b.start > ?2 " +
            "order by b.start desc")
    List<Booking> findBookingsOfItemOwnerInStateFUTURE(int userID, LocalDateTime dateTime);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 " +
            "and (b.start < ?2 or b.end > ?2) and b.status = 'WAITING' order by b.start desc")
    List<Booking> findBookingsOfItemOwnerInStateWAITING(int userID, LocalDateTime dateTime);

    @Query("select b from Booking as b join b.item as i where i.owner.id = ?1 " +
            "and (b.status = 'CANCELED' or b.status = 'REJECTED') order by b.start desc ")
    List<Booking> findBookingsOfItemOwnerInStateREJECTED(int userID);

    @Query(value = "select * from bookings where item_id = ?1 and status = 'APPROVED' " +
            "and start_date < ?2 order by start_date desc limit 1", nativeQuery = true)
    Optional<Booking> findPreviousItemBooking(int itemID, LocalDateTime dateTime);

    @Query(value = "select * from bookings where item_id = ?1 and status = 'APPROVED' " +
            "and start_date > ?2 order by start_date limit 1", nativeQuery = true)
    Optional<Booking> findNextItemBooking(int itemID, LocalDateTime dateTime);

    @Query(value = "select * from bookings where booker_id = ?1 and status = 'APPROVED' limit 1", nativeQuery = true)
    Optional<Booking> findOneApprovedBookingOfUser(int userID);

    @Query(value = "select * from bookings where item_id = ?1 and start_date < ?2 " +
            "and status = 'APPROVED' limit 1", nativeQuery = true)
    Optional<Booking> findOneApprovedBookingOfItemInPast(int itemID, LocalDateTime dateTime);
}
