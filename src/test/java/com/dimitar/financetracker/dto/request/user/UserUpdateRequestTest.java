package com.dimitar.financetracker.dto.request.user;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_shouldPassValidation() {
        UserUpdateRequest req = UserUpdateRequest.builder()
                .username("newusername")
                .email("new@email.com")
                .build();

        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(req);
        assertThat(violations).isEmpty();
    }

    @Test
    void emptyRequest_shouldPassValidation() {
        UserUpdateRequest req = UserUpdateRequest.builder().build();

        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(req);
        assertThat(violations).isEmpty();
    }

    @Test
    void nullFields_shouldPassValidation() {
        UserUpdateRequest req = UserUpdateRequest.builder()
                .username(null)
                .email(null)
                .build();

        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(req);
        assertThat(violations).isEmpty();
    }

    @Test
    void emptyStrings_shouldPassValidation() {
        UserUpdateRequest req = UserUpdateRequest.builder()
                .username("")
                .email("")
                .build();

        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(req);
        assertThat(violations).isEmpty();
    }

    @Test
    void tooLongUsername_shouldFailValidation() {
        String longUsername = "a".repeat(51); // assuming USERNAME_MAX_LENGTH is 50
        UserUpdateRequest req = UserUpdateRequest.builder()
                .username(longUsername)
                .email("user@email.com")
                .build();

        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    void tooLongEmail_shouldFailValidation() {
        String longEmail = "a".repeat(101) + "@mail.com"; // assuming EMAIL_MAX_LENGTH is 255
        UserUpdateRequest req = UserUpdateRequest.builder()
                .username("user")
                .email(longEmail)
                .build();

        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void invalidEmail_shouldFailValidation() {
        UserUpdateRequest req = UserUpdateRequest.builder()
                .username("user")
                .email("not-an-email")
                .build();

        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(req);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void validEmailFormats_shouldPassValidation() {
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@test.com"
        };

        for (String email : validEmails) {
            UserUpdateRequest req = UserUpdateRequest.builder()
                    .username("testuser")
                    .email(email)
                    .build();

            Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(req);
            assertThat(violations).isEmpty();
        }
    }

    @Test
    void invalidEmailFormats_shouldFailValidation() {
        String[] invalidEmails = {
            "plainaddress",
            "@missingdomain.com",
            "missing@.com",
            "missing.domain@.com",
            "two@@domain.com",
            "domain@domain@domain.com"
        };

        for (String email : invalidEmails) {
            UserUpdateRequest req = UserUpdateRequest.builder()
                    .username("testuser")
                    .email(email)
                    .build();

            Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(req);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        }
    }

    @Test
    void whitespaceOnlyFields_shouldPassValidation() {
        UserUpdateRequest req = UserUpdateRequest.builder()
                .username("   ")
                .email("   ")
                .build();

        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(req);
        // Should pass validation but fail @Email for email field
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }
}

