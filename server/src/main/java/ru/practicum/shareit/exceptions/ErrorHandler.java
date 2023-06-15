package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, BookingNotFoundException.class,
            IncorrectBookingApproverException.class, IncorrectOwnerInBookingException.class,
            ItemRequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final RuntimeException e) {
        log.error(e.getMessage(), e.getStackTrace());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({EmailAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(final RuntimeException e) {
        log.error(e.getMessage(), e.getStackTrace());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({IncorrectItemOwnerException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbiddenException(final RuntimeException e) {
        log.error(e.getMessage(), e.getStackTrace());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({BookingValidationException.class, UserNotBookedItemException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final RuntimeException e) {
        log.error(e.getMessage(), e.getStackTrace());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnexpectedException(final RuntimeException e) {
        log.error(e.getMessage(), e.getStackTrace());
        return Map.of("error", e.getMessage());
    }
}
