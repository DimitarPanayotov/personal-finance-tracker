package com.dimitar.financetracker.dto.request.transaction;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTransactionRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_shouldPassValidation() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .description("Test transaction")
                .transactionDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullCategoryId_shouldFailValidation() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(null)
                .amount(new BigDecimal("100.50"))
                .transactionDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Category is required");
    }

    @Test
    void nullAmount_shouldFailValidation() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(null)
                .transactionDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Amount is required");
    }

    @Test
    void amountTooSmall_shouldFailValidation() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("0.00"))
                .transactionDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Amount must be at least 0.01");
    }

    @Test
    void nullTransactionDate_shouldFailValidation() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .transactionDate(null)
                .build();

        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Transaction date is required");
    }

    @Test
    void descriptionTooLong_shouldFailValidation() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .description("A".repeat(256))
                .transactionDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Description must be less than");
    }

    @Test
    void nullDescription_shouldPassValidation() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .description(null)
                .transactionDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<CreateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void builderPattern_shouldWork() {
        LocalDate testDate = LocalDate.of(2023, 1, 1);
        BigDecimal testAmount = new BigDecimal("150.75");

        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(2L)
                .amount(testAmount)
                .description("Test description")
                .transactionDate(testDate)
                .build();

        assertThat(request.getCategoryId()).isEqualTo(2L);
        assertThat(request.getAmount()).isEqualTo(testAmount);
        assertThat(request.getDescription()).isEqualTo("Test description");
        assertThat(request.getTransactionDate()).isEqualTo(testDate);
    }

    @Test
    void equalsAndHashCode_shouldWork() {
        CreateTransactionRequest request1 = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .description("Test")
                .transactionDate(LocalDate.now())
                .build();

        CreateTransactionRequest request2 = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .description("Test")
                .transactionDate(LocalDate.now())
                .build();

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }
}

