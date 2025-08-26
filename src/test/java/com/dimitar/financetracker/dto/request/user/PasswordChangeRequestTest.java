package com.dimitar.financetracker.dto.request.user;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordChangeRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_shouldPassValidation() {
        PasswordChangeRequest req = PasswordChangeRequest.builder()
                .password("oldPassword123")
                .newPassword("newPassword123")
                .build();

        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(req);
        assertThat(violations).isEmpty();
    }

    @Test
    void blankCurrentPassword_shouldFailValidation() {
        PasswordChangeRequest req = PasswordChangeRequest.builder()
                .password("")
                .newPassword("newPassword123")
                .build();

        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void nullCurrentPassword_shouldFailValidation() {
        PasswordChangeRequest req = PasswordChangeRequest.builder()
                .password(null)
                .newPassword("newPassword123")
                .build();

        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void blankNewPassword_shouldFailValidation() {
        PasswordChangeRequest req = PasswordChangeRequest.builder()
                .password("oldPassword123")
                .newPassword("")
                .build();

        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("newPassword"));
    }

    @Test
    void nullNewPassword_shouldFailValidation() {
        PasswordChangeRequest req = PasswordChangeRequest.builder()
                .password("oldPassword123")
                .newPassword(null)
                .build();

        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("newPassword"));
    }

    @Test
    void shortNewPassword_shouldFailValidation() {
        PasswordChangeRequest req = PasswordChangeRequest.builder()
                .password("oldPassword123")
                .newPassword("123")
                .build();

        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("newPassword"));
    }

    @Test
    void whitespaceOnlyCurrentPassword_shouldFailValidation() {
        PasswordChangeRequest req = PasswordChangeRequest.builder()
                .password("   ")
                .newPassword("newPassword123")
                .build();

        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void whitespaceOnlyNewPassword_shouldFailValidation() {
        PasswordChangeRequest req = PasswordChangeRequest.builder()
                .password("oldPassword123")
                .newPassword("   ")
                .build();

        Set<ConstraintViolation<PasswordChangeRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("newPassword"));
    }
}

