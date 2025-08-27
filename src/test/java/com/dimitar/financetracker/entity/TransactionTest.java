package com.dimitar.financetracker.entity;

import com.dimitar.financetracker.model.CategoryType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionTest {

    private static Validator validator;
    private Transaction transaction;
    private User user;
    private Category category;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        category = Category.builder()
                .id(1L)
                .user(user)
                .name("Food & Dining")
                .type(CategoryType.EXPENSE)
                .color("#FF5733")
                .build();

        transaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(new BigDecimal("25.50"))
                .description("Lunch at restaurant")
                .transactionDate(LocalDate.now())
                .build();
    }

    @Test
    void validTransaction_shouldPassValidation() {
        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations).isEmpty();
    }

    @Test
    void builder_shouldCreateTransactionWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        Transaction transaction = Transaction.builder()
                .id(1L)
                .user(user)
                .category(category)
                .amount(new BigDecimal("100.00"))
                .description("Test transaction")
                .transactionDate(today)
                .createdAt(now)
                .build();

        assertThat(transaction.getId()).isEqualTo(1L);
        assertThat(transaction.getUser()).isEqualTo(user);
        assertThat(transaction.getCategory()).isEqualTo(category);
        assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(transaction.getDescription()).isEqualTo("Test transaction");
        assertThat(transaction.getTransactionDate()).isEqualTo(today);
        assertThat(transaction.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void nullUser_shouldFailValidation() {
        transaction.setUser(null);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("user"));
    }

    @Test
    void nullCategory_shouldFailValidation() {
        transaction.setCategory(null);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("category"));
    }

    @Test
    void nullAmount_shouldFailValidation() {
        transaction.setAmount(null);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    void zeroAmount_shouldFailValidation() {
        transaction.setAmount(BigDecimal.ZERO);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    void negativeAmount_shouldFailValidation() {
        transaction.setAmount(new BigDecimal("-10.00"));

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("amount"));
    }

    @Test
    void validMinimumAmount_shouldPassValidation() {
        transaction.setAmount(new BigDecimal("0.01"));

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("amount"))
                .isEmpty();
    }

    @Test
    void nullTransactionDate_shouldFailValidation() {
        transaction.setTransactionDate(null);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("transactionDate"));
    }

    @Test
    void tooLongDescription_shouldFailValidation() {
        String longDescription = "a".repeat(256); // DESCRIPTION_MAX_LENGTH is 255
        transaction.setDescription(longDescription);

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("description")
            && v.getMessage().contains("Description must be less than"));
    }

    @Test
    void emptyDescription_shouldPassValidation() {
        transaction.setDescription("");

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("description"))
                .isEmpty();
    }

    @Test
    void validLargeAmount_shouldPassValidation() {
        transaction.setAmount(new BigDecimal("999999.99"));

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("amount"))
                .isEmpty();
    }

    @Test
    void onCreate_shouldSetTimestamp() {
        Transaction newTransaction = new Transaction();
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        newTransaction.onCreate();

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(newTransaction.getCreatedAt()).isBetween(before, after);
    }

    @Test
    void onCreate_shouldSetCreatedAtAndUpdatedAt() {
        Transaction transaction = new Transaction();

        transaction.onCreate();

        assertThat(transaction.getCreatedAt()).isNotNull();
        assertThat(transaction.getUpdatedAt()).isNotNull();
        assertThat(transaction.getCreatedAt()).isEqualTo(transaction.getUpdatedAt());
        assertThat(transaction.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void onUpdate_shouldUpdateUpdatedAtField() {
        Transaction transaction = new Transaction();
        transaction.onCreate();
        LocalDateTime originalCreatedAt = transaction.getCreatedAt();
        LocalDateTime originalUpdatedAt = transaction.getUpdatedAt();

        // Wait a bit to ensure different timestamp
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        transaction.onUpdate();

        assertThat(transaction.getCreatedAt()).isEqualTo(originalCreatedAt); // Should not change
        assertThat(transaction.getUpdatedAt()).isNotEqualTo(originalUpdatedAt); // Should change
        assertThat(transaction.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void equals_shouldWorkCorrectlyWithSameData() {
        Transaction transaction1 = Transaction.builder()
                .id(1L)
                .user(user)
                .category(category)
                .amount(new BigDecimal("50.00"))
                .description("Test")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(1L)
                .user(user)
                .category(category)
                .amount(new BigDecimal("50.00"))
                .description("Test")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .build();

        assertThat(transaction1).isEqualTo(transaction2);
        assertThat(transaction1.hashCode()).isEqualTo(transaction2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentData() {
        Transaction transaction1 = Transaction.builder()
                .id(1L)
                .amount(new BigDecimal("50.00"))
                .description("Test1")
                .transactionDate(LocalDate.of(2023, 1, 1))
                .build();

        Transaction transaction2 = Transaction.builder()
                .id(2L)
                .amount(new BigDecimal("75.00"))
                .description("Test2")
                .transactionDate(LocalDate.of(2023, 1, 2))
                .build();

        assertThat(transaction1).isNotEqualTo(transaction2);
    }

    @Test
    void toString_shouldNotIncludeRelationships() {
        String toString = transaction.toString();

        assertThat(toString).contains("amount=25.50");
        assertThat(toString).contains("description=Lunch at restaurant");
        assertThat(toString).doesNotContain("user");
        assertThat(toString).doesNotContain("category");
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyTransaction() {
        Transaction emptyTransaction = new Transaction();

        assertThat(emptyTransaction.getId()).isNull();
        assertThat(emptyTransaction.getUser()).isNull();
        assertThat(emptyTransaction.getCategory()).isNull();
        assertThat(emptyTransaction.getAmount()).isNull();
        assertThat(emptyTransaction.getDescription()).isNull();
        assertThat(emptyTransaction.getTransactionDate()).isNull();
        assertThat(emptyTransaction.getCreatedAt()).isNull();
    }

    @Test
    void pastTransactionDate_shouldPassValidation() {
        transaction.setTransactionDate(LocalDate.of(2022, 1, 1));

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("transactionDate"))
                .isEmpty();
    }

    @Test
    void futureTransactionDate_shouldPassValidation() {
        transaction.setTransactionDate(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
        assertThat(violations)
                .filteredOn(v -> v.getPropertyPath().toString().equals("transactionDate"))
                .isEmpty();
    }
}
