package com.dimitar.financetracker.exception;

import com.dimitar.financetracker.dto.response.error.ErrorResponse;
import com.dimitar.financetracker.dto.response.error.ValidationErrorResponse;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.exception.budget.OverlappingBudgetException;
import com.dimitar.financetracker.exception.user.IncorrectPasswordException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleValidationErrors_returnsValidationErrorResponse() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("user", "username", "Username is required");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ValidationErrorResponse> response = handler.handleValidationErrors(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertTrue(response.getBody().getErrors().containsKey("username"));
    }

    @Test
    void handleIllegalArgument_returnsBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid input", response.getBody().getMessage());
    }

    @Test
    void handleOverlappingBudgetException_returnsConflict() {
        OverlappingBudgetException ex = new OverlappingBudgetException("Budget overlap detected");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicateExceptions(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Budget overlap detected", response.getBody().getMessage());
    }

    @Test
    void handleBudgetDoesNotExist_returnsNotFound() {
        BudgetDoesNotExistException ex = new BudgetDoesNotExistException("Budget not found");

        ResponseEntity<ErrorResponse> response = handler.handleNonExistExceptions(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Budget not found", response.getBody().getMessage());
    }

    @Test
    void handleBadCredentials_returnsUnauthorized() {
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        ResponseEntity<ErrorResponse> response = handler.handleAuthenticationErrors(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid username/email or password", response.getBody().getMessage());
    }

    @Test
    void handleIncorrectPassword_returnsUnauthorized() {
        IncorrectPasswordException ex = new IncorrectPasswordException("Incorrect password");

        ResponseEntity<ErrorResponse> response = handler.handleAuthenticationErrors(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Incorrect password", response.getBody().getMessage());
    }

    @Test
    void handleAccessDenied_returnsForbidden() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(ex, request);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("permission"));
    }

    @Test
    void handleMethodNotSupported_returnsMethodNotAllowed() {
        HttpRequestMethodNotSupportedException ex =
            new HttpRequestMethodNotSupportedException("POST", Set.of("GET", "PUT"));

        ResponseEntity<ErrorResponse> response = handler.handleMethodNotSupported(ex, request);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("POST"));
    }

    @Test
    void handleMessageNotReadable_returnsBadRequest() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getCause()).thenReturn(new RuntimeException("Cannot deserialize"));

        ResponseEntity<ErrorResponse> response = handler.handleMessageNotReadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Invalid data format"));
    }

    @Test
    void handleMissingParameter_returnsBadRequest() {
        MissingServletRequestParameterException ex =
            new MissingServletRequestParameterException("userId", "Long");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParameter(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("userId"));
    }

    @Test
    void handleTypeMismatch_returnsBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("id");
        when(ex.getValue()).thenReturn("abc");
        when(ex.getRequiredType()).thenReturn((Class) Long.class);

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("id"));
        assertTrue(response.getBody().getMessage().contains("abc"));
    }

    @Test
    void handleDataIntegrityViolation_returnsConflict() {
        DataIntegrityViolationException ex =
            new DataIntegrityViolationException("unique constraint violation");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("already exists"));
    }

    @Test
    void handleNullPointer_returnsInternalServerError() {
        NullPointerException ex = new NullPointerException("Null pointer");

        ResponseEntity<ErrorResponse> response = handler.handleNullPointer(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("unexpected error"));
    }

    @Test
    void handleGeneralException_returnsInternalServerError() {
        Exception ex = new Exception("Some unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("unexpected error"));
    }
}
