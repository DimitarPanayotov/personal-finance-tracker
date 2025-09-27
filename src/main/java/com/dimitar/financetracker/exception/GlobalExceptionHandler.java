package com.dimitar.financetracker.exception;

import com.dimitar.financetracker.dto.response.error.ErrorResponse;
import com.dimitar.financetracker.dto.response.error.ValidationErrorResponse;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.exception.user.DuplicateEmailException;
import com.dimitar.financetracker.exception.user.DuplicateUsernameException;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.exception.transaction.TransactionDoesNotExistException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.dimitar.financetracker.util.HttpStatuses.BAD_REQUEST_STATUS_CODE;
import static com.dimitar.financetracker.util.HttpStatuses.CONFLICT_STATUS_CODE;
import static com.dimitar.financetracker.util.HttpStatuses.NOT_FOUND;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );

        ValidationErrorResponse response = new ValidationErrorResponse(
            "Validation failed for user registration",
            request.getRequestURI(),
            errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
        IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
            BAD_REQUEST_STATUS_CODE,
            "Bad Request",
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({DuplicateUsernameException.class, DuplicateEmailException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateExceptions(
        RuntimeException ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
            CONFLICT_STATUS_CODE,
            "Conflict",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({UserDoesNotExistException.class, CategoryDoesNotExistException.class, TransactionDoesNotExistException.class})
    public ResponseEntity<ErrorResponse> handleNonExistExceptions(
        RuntimeException ex, HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
            NOT_FOUND,
            "Not found",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}