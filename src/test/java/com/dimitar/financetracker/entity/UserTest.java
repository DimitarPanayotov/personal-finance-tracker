package com.dimitar.financetracker.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private static Validator validator;
    private User user;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    void validUser_shouldPassValidation() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isEmpty();
    }

    @Test
    void builder_shouldCreateUserWithAllFields() {
        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .createdAt(now)
                .updatedAt(now)
                .categories(new ArrayList<>())
                .transactions(new ArrayList<>())
                .budgets(new ArrayList<>())
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
        assertThat(user.getCategories()).isNotNull();
        assertThat(user.getTransactions()).isNotNull();
        assertThat(user.getBudgets()).isNotNull();
    }

    @Test
    void blankUsername_shouldFailValidation() {
        user.setUsername("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    void nullUsername_shouldFailValidation() {
        user.setUsername(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    void tooLongUsername_shouldFailValidation() {
        String longUsername = "a".repeat(51); // assuming USERNAME_MAX_LENGTH is 50
        user.setUsername(longUsername);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("username"));
    }

    @Test
    void blankEmail_shouldFailValidation() {
        user.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void nullEmail_shouldFailValidation() {
        user.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void invalidEmail_shouldFailValidation() {
        user.setEmail("not-an-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void tooLongEmail_shouldFailValidation() {
        String longEmail = "a".repeat(101) + "@example.com"; // assuming EMAIL_MAX_LENGTH is 255
        user.setEmail(longEmail);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void blankPassword_shouldFailValidation() {
        user.setPassword("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void nullPassword_shouldFailValidation() {
        user.setPassword(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void shortPassword_shouldFailValidation() {
        user.setPassword("123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
    }

    @Test
    void validEmailFormats_shouldPassValidation() {
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@test.com",
            "user_name@example-domain.com"
        };

        for (String email : validEmails) {
            user.setEmail(email);
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("email"))
                .isEmpty();
        }
    }

    @Test
    void invalidEmailFormats_shouldFailValidation() {
        String[] invalidEmails = {
            "plainaddress",
            "@example.com",
            "user@",
            "user..name@example.com",
            "user@.com",
            "user@domain@domain.com"
        };

        for (String email : invalidEmails) {
            user.setEmail(email);
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        }
    }

    @Test
    void onCreate_shouldSetTimestamps() {
        User newUser = new User();
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        newUser.onCreate();

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(newUser.getCreatedAt()).isBetween(before, after);
        assertThat(newUser.getUpdatedAt()).isBetween(before, after);
        assertThat(newUser.getCreatedAt()).isEqualTo(newUser.getUpdatedAt());
    }

    @Test
    void onUpdate_shouldUpdateTimestamp() throws InterruptedException {
        user.onCreate();
        LocalDateTime originalCreatedAt = user.getCreatedAt();
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();

        Thread.sleep(10); // Small delay to ensure different timestamps
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        user.onUpdate();

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(user.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(user.getUpdatedAt()).isBetween(before, after);
        assertThat(user.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void equals_shouldWorkCorrectlyWithSameData() {
        User user1 = User.builder()
                .id(1L)
                .username("user")
                .email("user@example.com")
                .password("password")
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("user")
                .email("user@example.com")
                .password("password")
                .build();

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentData() {
        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .password("password1")
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .password("password2")
                .build();

        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void toString_shouldNotIncludeRelationships() {
        user.setCategories(new ArrayList<>());
        user.setTransactions(new ArrayList<>());
        user.setBudgets(new ArrayList<>());

        String toString = user.toString();

        assertThat(toString).contains("username=testuser");
        assertThat(toString).contains("email=test@example.com");
        assertThat(toString).doesNotContain("categories");
        assertThat(toString).doesNotContain("transactions");
        assertThat(toString).doesNotContain("budgets");
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyUser() {
        User emptyUser = new User();

        assertThat(emptyUser.getId()).isNull();
        assertThat(emptyUser.getUsername()).isNull();
        assertThat(emptyUser.getEmail()).isNull();
        assertThat(emptyUser.getPassword()).isNull();
        assertThat(emptyUser.getCreatedAt()).isNull();
        assertThat(emptyUser.getUpdatedAt()).isNull();
    }
}

