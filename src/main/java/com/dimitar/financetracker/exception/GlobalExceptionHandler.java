package com.dimitar.financetracker.exception;

import com.dimitar.financetracker.dto.response.error.ErrorResponse;
import com.dimitar.financetracker.dto.response.error.ValidationErrorResponse;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.exception.user.DuplicateEmailException;
import com.dimitar.financetracker.exception.user.DuplicateUsernameException;
import com.dimitar.financetracker.exception.user.IncorrectPasswordException;
import com.dimitar.financetracker.exception.user.UserAlreadyExistsException;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.exception.transaction.TransactionDoesNotExistException;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.exception.budget.OverlappingBudgetException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

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
            "Validation failed",
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

    @ExceptionHandler({
        DuplicateUsernameException.class,
        DuplicateEmailException.class,
        UserAlreadyExistsException.class,
        OverlappingBudgetException.class
    })
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

    @ExceptionHandler({
        UserDoesNotExistException.class,
        CategoryDoesNotExistException.class,
        TransactionDoesNotExistException.class,
        BudgetDoesNotExistException.class
    })
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

    @ExceptionHandler({
        BadCredentialsException.class,
        IncorrectPasswordException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationErrors(
        RuntimeException ex, HttpServletRequest request) {

        String message = ex instanceof BadCredentialsException
            ? "Invalid username/email or password"
            : ex.getMessage();

        ErrorResponse response = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            message,
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
        AuthenticationException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            "Authentication failed: " + ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
        AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Forbidden",
            "You don't have permission to access this resource",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
        HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            "Method Not Allowed",
            String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
                ex.getMethod(), ex.getSupportedHttpMethods()),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
        HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
            "Unsupported Media Type",
            String.format("Content-Type '%s' is not supported. Supported types: %s",
                ex.getContentType(), ex.getSupportedMediaTypes()),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(
        HttpMessageNotReadableException ex, HttpServletRequest request) {

        String message = "Malformed JSON request";
        if (ex.getCause() != null) {
            String causeMessage = ex.getCause().getMessage();
            if (causeMessage != null && !causeMessage.isEmpty()) {
                if (causeMessage.contains("Cannot deserialize")) {
                    message = "Invalid data format in request body";
                } else if (causeMessage.contains("Unexpected character")) {
                    message = "Invalid JSON syntax in request body";
                }
            }
        }

        ErrorResponse response = new ErrorResponse(
            BAD_REQUEST_STATUS_CODE,
            "Bad Request",
            message,
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
        MissingServletRequestParameterException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
            BAD_REQUEST_STATUS_CODE,
            "Bad Request",
            String.format("Required parameter '%s' is missing", ex.getParameterName()),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String message = String.format("Parameter '%s' has invalid value '%s'",
            ex.getName(), ex.getValue());

        if (ex.getRequiredType() != null) {
            message += String.format(". Expected type: %s", ex.getRequiredType().getSimpleName());
        }

        ErrorResponse response = new ErrorResponse(
            BAD_REQUEST_STATUS_CODE,
            "Bad Request",
            message,
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
        NoHandlerFoundException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
            NOT_FOUND,
            "Not Found",
            String.format("Endpoint '%s' not found", ex.getRequestURL()),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
        DataIntegrityViolationException ex, HttpServletRequest request) {

        String message = "Database constraint violation";
        String exMessage = ex.getMessage();

        if (exMessage != null) {
            if (exMessage.contains("unique constraint") || exMessage.contains("duplicate key")) {
                message = "A record with this value already exists";
            } else if (exMessage.contains("foreign key constraint")) {
                message = "Cannot perform this operation due to related records";
            } else if (exMessage.contains("not-null constraint")) {
                message = "Required field is missing";
            }
        }

        ErrorResponse response = new ErrorResponse(
            CONFLICT_STATUS_CODE,
            "Data Conflict",
            message,
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointer(
        NullPointerException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please contact support if this persists.",
            request.getRequestURI()
        );

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
        Exception ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getRequestURI()
        );

       System.err.println("Unhandled exception: " + ex.getClass().getName());
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}