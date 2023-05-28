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
import ru.practicum.shareit.booking.dto.IncomeBookingDto;
import ru.practicum.shareit.booking.dto.OutcomeBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public OutcomeBookingDto create(@RequestHeader("X-Sharer-User-Id") int userID,
                                    @Valid @RequestBody IncomeBookingDto bookingDto) {
        return bookingService.addBooking(userID, bookingDto);
    }

    @PatchMapping("/{bookingID}")
    public OutcomeBookingDto approveBooking(@RequestHeader("X-Sharer-User-Id") int userID,
                                            @PathVariable int bookingID,
                                            @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(userID, bookingID, approved);
    }

    @GetMapping("/{bookingID}")
    public OutcomeBookingDto findBookingByID(@RequestHeader("X-Sharer-User-Id") int userID,
                                             @PathVariable int bookingID) {
        return bookingService.getBookingByID(userID, bookingID);
    }

    @GetMapping
    public List<OutcomeBookingDto> findBookingsOfUserInState(@RequestHeader("X-Sharer-User-Id") int userID,
                                                             @RequestParam(required = false) String state) {
        return bookingService.getBookingsOfUserByState(userID, state);
    }

    @GetMapping("/owner")
    public List<OutcomeBookingDto> findBookingsOfItemOwnerByState(@RequestHeader("X-Sharer-User-Id") int userID,
                                                                  @RequestParam(required = false) String state) {
        return bookingService.getBookingsOfUserItemsByState(userID, state);
    }
}
