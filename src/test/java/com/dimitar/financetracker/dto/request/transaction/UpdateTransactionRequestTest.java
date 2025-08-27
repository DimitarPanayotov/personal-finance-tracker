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

class UpdateTransactionRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_shouldPassValidation() {
        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .description("Updated transaction")
                .transactionDate(LocalDate.now())
                .build();

        Set<ConstraintViolation<UpdateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void allNullFields_shouldPassValidation() {
        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .categoryId(null)
                .amount(null)
                .description(null)
                .transactionDate(null)
                .build();

        Set<ConstraintViolation<UpdateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void amountTooSmall_shouldFailValidation() {
        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .amount(new BigDecimal("0.00"))
                .build();

        Set<ConstraintViolation<UpdateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Amount must be at least 0.01");
    }

    @Test
    void descriptionTooLong_shouldFailValidation() {
        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .description("A".repeat(256))
                .build();

        Set<ConstraintViolation<UpdateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Description must be less than");
    }

    @Test
    void partialUpdate_shouldWork() {
        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .amount(new BigDecimal("200.00"))
                .description("Only updating amount and description")
                .build();

        Set<ConstraintViolation<UpdateTransactionRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
        assertThat(request.getCategoryId()).isNull();
        assertThat(request.getTransactionDate()).isNull();
        assertThat(request.getAmount()).isEqualTo(new BigDecimal("200.00"));
        assertThat(request.getDescription()).isEqualTo("Only updating amount and description");
    }

    @Test
    void builderPattern_shouldWork() {
        LocalDate testDate = LocalDate.of(2023, 1, 1);
        BigDecimal testAmount = new BigDecimal("150.75");

        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
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
        UpdateTransactionRequest request1 = UpdateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .description("Test")
                .transactionDate(LocalDate.now())
                .build();

        UpdateTransactionRequest request2 = UpdateTransactionRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.50"))
                .description("Test")
                .transactionDate(LocalDate.now())
                .build();

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }
}

