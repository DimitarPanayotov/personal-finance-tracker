package com.dimitar.financetracker.dto.request.user;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserLoginRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequestWithUsername_shouldPassValidation() {
        UserLoginRequest req = UserLoginRequest.builder()
                .usernameOrEmail("testuser")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(req);
        assertThat(violations).isEmpty();
    }

    @Test
    void validRequestWithEmail_shouldPassValidation() {
        UserLoginRequest req = UserLoginRequest.builder()
                .usernameOrEmail("test@example.com")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(req);
        assertThat(violations).isEmpty();
    }

    @Test
    void blankUsernameOrEmail_shouldFailValidation() {
        UserLoginRequest req = UserLoginRequest.builder()
                .usernameOrEmail("")
                .password("password123")
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("usernameOrEmail"));
    }

    @Test
    void nullUsernameOrEmail_shouldFailValidation() {
        UserLoginRequest req = UserLoginRequest.builder()
                .usernameOrEmail(null)
                .password("password123")
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("usernameOrEmail"));
    }

    @Test
    void blankPassword_shouldFailValidation() {
        UserLoginRequest req = UserLoginRequest.builder()
                .usernameOrEmail("testuser")
                .password("")
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void nullPassword_shouldFailValidation() {
        UserLoginRequest req = UserLoginRequest.builder()
                .usernameOrEmail("testuser")
                .password(null)
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void whitespaceOnlyCredentials_shouldFailValidation() {
        UserLoginRequest req = UserLoginRequest.builder()
                .usernameOrEmail("   ")
                .password("   ")
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(req);
        assertThat(violations).hasSize(2);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("usernameOrEmail"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void bothFieldsNull_shouldFailValidation() {
        UserLoginRequest req = UserLoginRequest.builder()
                .usernameOrEmail(null)
                .password(null)
                .build();

        Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(req);
        assertThat(violations).hasSize(2);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("usernameOrEmail"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }
}

