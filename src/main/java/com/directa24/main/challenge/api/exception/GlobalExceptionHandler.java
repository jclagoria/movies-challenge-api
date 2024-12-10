package com.directa24.main.challenge.api.exception;

import com.directa24.main.challenge.api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles invalid input exceptions, such as threshold values less than or equal to zero.
     *
     * @param ex The exception object.
     * @return A ResponseEntity with a descriptive error message and HTTP 400 status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("Invalid Input", ex.getMessage()));
    }

    /**
     * Handles response parsing errors, such as invalid response structures from the API.
     *
     * @param ex The exception object.
     * @return A ResponseEntity with a descriptive error message and HTTP 500 status.
     */
    @ExceptionHandler(ResponseParsingException.class)
    public ResponseEntity<ErrorResponse> handleResponseParsingException(ResponseParsingException ex) {
        log.error("Response parsing exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Parsing Error", ex.getMessage()));
    }

    /**
     * Handles generic exceptions for any unanticipated errors in the application.
     *
     * @param ex The exception object.
     * @return A ResponseEntity with a descriptive error message and HTTP 500 status.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGenericException(RuntimeException ex) {
        log.error("Unexpected exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal Server Error", "An unexpected error occurred."));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleServerWebInputException(ServerWebInputException ex) {
        log.error("Unexpected exception: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Parameter error", ex.getCause()
                        != null ? ex.getCause().getLocalizedMessage() : ex.getReason()));
    }

}
