package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query(value = "select * from bookings where booker_id = :id order by start_date desc", nativeQuery = true)
    List<Booking> findBookingsOfUserInStateALL(@Param("id") int userID, Pageable pageable);

    @Query(value = "select * from bookings where booker_id = :id and (status = 'REJECTED' or status = 'CANCELED') " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findBookingsOfUserInStateREJECTED(@Param("id") int userID, Pageable pageable);

    @Query(value = "select * from bookings where booker_id = :id and end_date < :moment " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findBookingsOfUserInStatePAST(@Param("id") int userID,
                                                @Param("moment") LocalDateTime dateTime,
                                                Pageable pageable);

    @Query(value = "select * from bookings where booker_id = :id and start_date > :moment " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findBookingsOfUserInStateFUTURE(@Param("id") int userID,
                                                  @Param("moment") LocalDateTime dateTime,
                                                  Pageable pageable);

    @Query(value = "select * from bookings where booker_id = :id " +
            "and (start_date < :moment and end_date > :moment) " +
            "order by id", nativeQuery = true)
    List<Booking> findBookingsOfUserInStateCURRENT(@Param("id") int userID,
                                                   @Param("moment") LocalDateTime dateTime,
                                                   Pageable pageable);

    @Query(value = "select * from bookings where booker_id = :id and status = 'WAITING' " +
            "and (start_date < :moment or end_date > :moment) " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findBookingsOfUserInStateWAITING(@Param("id") int userID,
                                                   @Param("moment") LocalDateTime dateTime,
                                                   Pageable pageable);

    @Query("select b from Booking as b join b.item as i where i.owner.id = :id order by b.start desc")
    List<Booking> findBookingsOfItemOwnerInStateALL(@Param("id") int userID, Pageable pageable);

    @Query("select b from Booking as b join b.item as i where i.owner.id = :id " +
            "and (b.start < :moment and b.end > :moment) order by b.id")
    List<Booking> findBookingsOfItemOwnerInStateCURRENT(@Param("id") int userID,
                                                        @Param("moment") LocalDateTime dateTime,
                                                        Pageable pageable);

    @Query("select b from Booking as b join b.item as i where i.owner.id = :id and b.end < :moment " +
            "order by b.start desc")
    List<Booking> findBookingsOfItemOwnerInStatePAST(@Param("id") int userID,
                                                     @Param("moment") LocalDateTime dateTime,
                                                     Pageable pageable);

    @Query("select b from Booking as b join b.item as i where i.owner.id = :id and b.start > :moment " +
            "order by b.start desc")
    List<Booking> findBookingsOfItemOwnerInStateFUTURE(@Param("id") int userID,
                                                       @Param("moment") LocalDateTime dateTime,
                                                       Pageable pageable);

    @Query("select b from Booking as b join b.item as i where i.owner.id = :id " +
            "and (b.start < :moment or b.end > :moment) and b.status = 'WAITING' order by b.start desc")
    List<Booking> findBookingsOfItemOwnerInStateWAITING(@Param("id") int userID,
                                                        @Param("moment") LocalDateTime dateTime,
                                                        Pageable pageable);

    @Query("select b from Booking as b join b.item as i where i.owner.id = :id " +
            "and (b.status = 'CANCELED' or b.status = 'REJECTED') order by b.start desc ")
    List<Booking> findBookingsOfItemOwnerInStateREJECTED(@Param("id") int userID, Pageable pageable);

    @Query(value = "select * from bookings where item_id = :id and status = 'APPROVED' " +
            "and start_date < :moment order by start_date desc limit 1", nativeQuery = true)
    Optional<Booking> findPreviousItemBooking(@Param("id") int itemID,
                                              @Param("moment") LocalDateTime dateTime);

    @Query(value = "select * from bookings where item_id = :id and status = 'APPROVED' " +
            "and start_date > :moment order by start_date limit 1", nativeQuery = true)
    Optional<Booking> findNextItemBooking(@Param("id") int itemID,
                                          @Param("moment") LocalDateTime dateTime);

    @Query(value = "select * from bookings where booker_id = :id and status = 'APPROVED' limit 1", nativeQuery = true)
    Optional<Booking> findOneApprovedBookingOfUser(@Param("id") int userID);

    @Query(value = "select * from bookings where item_id = :id and start_date < :moment " +
            "and status = 'APPROVED' limit 1", nativeQuery = true)
    Optional<Booking> findOneApprovedBookingOfItemInPast(@Param("id") int itemID,
                                                         @Param("moment") LocalDateTime dateTime);
}
