package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.IncomeBookingDTO;
import ru.practicum.shareit.booking.dto.OutcomeBookingDTO;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public OutcomeBookingDTO create(@RequestHeader("X-Sharer-User-Id") int userID,
                                    @RequestBody IncomeBookingDTO bookingDto) {
        log.info("Server : POST to /bookings from userID {} with {}", userID, bookingDto.toString());
        return bookingService.addBooking(userID, bookingDto);
    }

    @PatchMapping("/{bookingID}")
    public OutcomeBookingDTO approveBooking(@RequestHeader("X-Sharer-User-Id") int userID,
                                            @PathVariable int bookingID,
                                            @RequestParam boolean approved) {
        log.info("Server : PATCH to /bookings/{} from userID {} with approved = {}", bookingID, userID, approved);
        return bookingService.changeBookingStatus(userID, bookingID, approved);
    }

    @GetMapping("/{bookingID}")
    public OutcomeBookingDTO findBookingByID(@RequestHeader("X-Sharer-User-Id") int userID,
                                             @PathVariable int bookingID) {
        log.info("Server : GET to /bookings/{} from userID {}", bookingID, userID);
        return bookingService.getBookingByID(userID, bookingID);
    }

    @GetMapping
    public List<OutcomeBookingDTO> findBookingsOfUserInState(@RequestHeader("X-Sharer-User-Id") int userID,
                                                             @RequestParam(defaultValue = "ALL") String state,
                                                             @RequestParam(defaultValue = "0") int from,
                                                             @RequestParam(defaultValue = "5") int size) {
        log.info("Server : GET to /bookings from userID {} with state {} , from {} , size {}", userID, state, from, size);
        return bookingService.getBookingsOfUserByState(userID, state, from, size);
    }

    @GetMapping("/owner")
    public List<OutcomeBookingDTO> findBookingsOfItemOwnerByState(@RequestHeader("X-Sharer-User-Id") int userID,
                                                                  @RequestParam(defaultValue = "ALL") String state,
                                                                  @RequestParam(defaultValue = "0") int from,
                                                                  @RequestParam(defaultValue = "5") int size) {
        log.info("Server : GET to /bookings/owner from userID {} with state {} , from {} , size {}", userID, state, from, size);
        return bookingService.getBookingsOfUserItemsByState(userID, state, from, size);
    }
}
