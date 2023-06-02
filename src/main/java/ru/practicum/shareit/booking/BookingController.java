package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingStateRequest;
import ru.practicum.shareit.booking.dto.IncomeBookingDTO;
import ru.practicum.shareit.booking.dto.OutcomeBookingDTO;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public OutcomeBookingDTO create(@RequestHeader("X-Sharer-User-Id") int userID,
                                    @Valid @RequestBody IncomeBookingDTO bookingDto) {
        return bookingService.addBooking(userID, bookingDto);
    }

    @PatchMapping("/{bookingID}")
    public OutcomeBookingDTO approveBooking(@RequestHeader("X-Sharer-User-Id") int userID,
                                            @PathVariable int bookingID,
                                            @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(userID, bookingID, approved);
    }

    @GetMapping("/{bookingID}")
    public OutcomeBookingDTO findBookingByID(@RequestHeader("X-Sharer-User-Id") int userID,
                                             @PathVariable int bookingID) {
        return bookingService.getBookingByID(userID, bookingID);
    }

    @GetMapping
    public List<OutcomeBookingDTO> findBookingsOfUserInState(@RequestHeader("X-Sharer-User-Id") int userID,
                                                             @RequestParam(required = false, defaultValue = "ALL") String state,
                                                             @RequestParam(defaultValue = "0", required = false) int from,
                                                             @RequestParam(defaultValue = "5", required = false) int size) {
        BookingStateRequest stateRequest = BookingStateRequest.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        if (from < 0 || size <= 0) {
            throw new ValidationException("Page or size can't be negative");
        }
        return bookingService.getBookingsOfUserByState(userID, stateRequest.name(), from, size);
    }

    @GetMapping("/owner")
    public List<OutcomeBookingDTO> findBookingsOfItemOwnerByState(@RequestHeader("X-Sharer-User-Id") int userID,
                                                                  @RequestParam(required = false, defaultValue = "ALL") String state,
                                                                  @RequestParam(defaultValue = "0", required = false) int from,
                                                                  @RequestParam(defaultValue = "5", required = false) int size) {
        BookingStateRequest stateRequest = BookingStateRequest.from(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        if (from < 0 || size <= 0) {
            throw new ValidationException("Page or size can't be negative");
        }
        return bookingService.getBookingsOfUserItemsByState(userID, stateRequest.name(), from, size);
    }
}
