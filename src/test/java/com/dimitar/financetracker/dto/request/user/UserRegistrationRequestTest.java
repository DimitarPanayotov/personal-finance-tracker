package com.dimitar.financetracker.dto.request.user;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegistrationRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_shouldPassValidation() {
        UserRegistrationRequest req = UserRegistrationRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(req);
        assertThat(violations).isEmpty();
    }

    @Test
    void blankUsername_shouldFailValidation() {
        UserRegistrationRequest req = UserRegistrationRequest.builder()
                .username("")
                .email("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    void invalidEmail_shouldFailValidation() {
        UserRegistrationRequest req = UserRegistrationRequest.builder()
                .username("testuser")
                .email("not-an-email")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void shortPassword_shouldFailValidation() {
        UserRegistrationRequest req = UserRegistrationRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("123")
                .build();

        Set<ConstraintViolation<UserRegistrationRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }
}

