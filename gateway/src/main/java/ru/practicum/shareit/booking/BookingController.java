package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.dto.BookingStateRequest;
import ru.practicum.shareit.exceptions.PageValidationException;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userID,
                                         @Valid @RequestBody BookingDTO bookingDto) {
        BookingDtoValidator.validate(bookingDto);
        log.info("Gateway : POST to /bookings from userID {} with {}", userID, bookingDto.toString());
        return bookingClient.addBooking(userID, bookingDto);
    }

    @PatchMapping("/{bookingID}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") int userID,
                                                 @PathVariable int bookingID,
                                                 @RequestParam boolean approved) {
        log.info("Gateway : PATCH to /bookings/{} from userID {} with approved = {}", bookingID, userID, approved);
        return bookingClient.changeBookingStatus(userID, bookingID, approved);
    }

    @GetMapping("/{bookingID}")
    public ResponseEntity<Object> findBookingByID(@RequestHeader("X-Sharer-User-Id") int userID,
                                                  @PathVariable int bookingID) {
        log.info("Gateway : GET to /bookings/{} from userID {}", bookingID, userID);
        return bookingClient.getBookingByID(userID, bookingID);
    }

    @GetMapping
    public ResponseEntity<Object> findBookingsOfUserInState(@RequestHeader("X-Sharer-User-Id") int userID,
                                                            @RequestParam(defaultValue = "ALL") String state,
                                                            @RequestParam(defaultValue = "0") int from,
                                                            @RequestParam(defaultValue = "5") int size) {
        BookingStateRequest stateRequest = BookingStateRequest.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        if (from < 0 || size <= 0) {
            throw new PageValidationException("Page or size can't be negative");
        }
        log.info("Gateway : GET to /bookings from userID {} with state {} , from {} , size {}", userID, state, from, size);
        return bookingClient.getBookingsOfUserByState(userID, stateRequest, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingsOfItemOwnerByState(@RequestHeader("X-Sharer-User-Id") int userID,
                                                                 @RequestParam(defaultValue = "ALL") String state,
                                                                 @RequestParam(defaultValue = "0") int from,
                                                                 @RequestParam(defaultValue = "5") int size) {
        BookingStateRequest stateRequest = BookingStateRequest.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        if (from < 0 || size <= 0) {
            throw new PageValidationException("Page or size can't be negative");
        }
        log.info("Gateway : GET to /bookings/owner from userID {} with state {} , from {} , size {}", userID, state, from, size);
        return bookingClient.getBookingsOfUserItemsByState(userID, stateRequest, from, size);
    }
}
