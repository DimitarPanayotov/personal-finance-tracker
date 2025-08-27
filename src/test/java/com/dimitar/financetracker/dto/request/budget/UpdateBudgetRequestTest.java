package com.dimitar.financetracker.dto.request.budget;

import com.dimitar.financetracker.model.BudgetPeriod;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateBudgetRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid UpdateBudgetRequest with all fields")
    void shouldCreateValidUpdateBudgetRequestWithAllFields() {
        // Given
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("150.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .build();

        // When
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.getCategoryId()).isEqualTo(1L);
        assertThat(request.getAmount()).isEqualTo(new BigDecimal("150.00"));
        assertThat(request.getPeriod()).isEqualTo(BudgetPeriod.MONTHLY);
        assertThat(request.getStartDate()).isEqualTo(LocalDate.now());
        assertThat(request.getEndDate()).isEqualTo(LocalDate.now().plusDays(30));
    }

    @Test
    @DisplayName("Should create valid UpdateBudgetRequest with no fields (all optional)")
    void shouldCreateValidUpdateBudgetRequestWithNoFields() {
        // Given
        UpdateBudgetRequest request = UpdateBudgetRequest.builder().build();

        // When
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.getCategoryId()).isNull();
        assertThat(request.getAmount()).isNull();
        assertThat(request.getPeriod()).isNull();
        assertThat(request.getStartDate()).isNull();
        assertThat(request.getEndDate()).isNull();
    }

    @Test
    @DisplayName("Should create valid UpdateBudgetRequest with only amount")
    void shouldCreateValidUpdateBudgetRequestWithOnlyAmount() {
        // Given
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .amount(new BigDecimal("75.50"))
                .build();

        // When
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.getAmount()).isEqualTo(new BigDecimal("75.50"));
    }

    @Test
    @DisplayName("Should create valid UpdateBudgetRequest with only categoryId")
    void shouldCreateValidUpdateBudgetRequestWithOnlyCategoryId() {
        // Given
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .categoryId(5L)
                .build();

        // When
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.getCategoryId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Should fail validation when amount is zero")
    void shouldFailValidationWhenAmountIsZero() {
        // Given
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .amount(BigDecimal.ZERO)
                .build();

        // When
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Budget amount must be at least 0.01");
    }

    @Test
    @DisplayName("Should fail validation when amount is negative")
    void shouldFailValidationWhenAmountIsNegative() {
        // Given
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .amount(new BigDecimal("-25.00"))
                .build();

        // When
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Budget amount must be at least 0.01");
    }

    @Test
    @DisplayName("Should accept minimum valid amount")
    void shouldAcceptMinimumValidAmount() {
        // Given
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .amount(new BigDecimal("0.01"))
                .build();

        // When
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.getAmount()).isEqualTo(new BigDecimal("0.01"));
    }

    @Test
    @DisplayName("Should accept large valid amount")
    void shouldAcceptLargeValidAmount() {
        // Given
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .amount(new BigDecimal("999999.99"))
                .build();

        // When
        Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.getAmount()).isEqualTo(new BigDecimal("999999.99"));
    }

    @Test
    @DisplayName("Should test all budget periods")
    void shouldTestAllBudgetPeriods() {
        for (BudgetPeriod period : BudgetPeriod.values()) {
            // Given
            UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                    .period(period)
                    .build();

            // When
            Set<ConstraintViolation<UpdateBudgetRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
            assertThat(request.getPeriod()).isEqualTo(period);
        }
    }

    @Test
    @DisplayName("Should test builder pattern functionality")
    void shouldTestBuilderPattern() {
        // Given & When
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .categoryId(10L)
                .amount(new BigDecimal("500.25"))
                .period(BudgetPeriod.QUARTERLY)
                .startDate(LocalDate.of(2024, 4, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .build();

        // Then
        assertThat(request.getCategoryId()).isEqualTo(10L);
        assertThat(request.getAmount()).isEqualTo(new BigDecimal("500.25"));
        assertThat(request.getPeriod()).isEqualTo(BudgetPeriod.QUARTERLY);
        assertThat(request.getStartDate()).isEqualTo(LocalDate.of(2024, 4, 1));
        assertThat(request.getEndDate()).isEqualTo(LocalDate.of(2024, 6, 30));
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsAndHashCode() {
        // Given
        UpdateBudgetRequest request1 = UpdateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .build();

        UpdateBudgetRequest request2 = UpdateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .build();

        UpdateBudgetRequest request3 = UpdateBudgetRequest.builder()
                .categoryId(2L)
                .amount(new BigDecimal("200.00"))
                .period(BudgetPeriod.WEEKLY)
                .build();

        // Then
        assertThat(request1).isEqualTo(request2);
        assertThat(request1).isNotEqualTo(request3);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    @DisplayName("Should test toString method")
    void shouldTestToStringMethod() {
        // Given
        UpdateBudgetRequest request = UpdateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.of(2024, 1, 1))
                .build();

        // When
        String toString = request.toString();

        // Then
        assertThat(toString).contains("UpdateBudgetRequest");
        assertThat(toString).contains("categoryId=1");
        assertThat(toString).contains("amount=100.00");
        assertThat(toString).contains("period=MONTHLY");
        assertThat(toString).contains("startDate=2024-01-01");
    }

    @Test
    @DisplayName("Should handle partial updates correctly")
    void shouldHandlePartialUpdatesCorrectly() {
        // Test updating only period
        UpdateBudgetRequest periodOnly = UpdateBudgetRequest.builder()
                .period(BudgetPeriod.YEARLY)
                .build();

        Set<ConstraintViolation<UpdateBudgetRequest>> violations1 = validator.validate(periodOnly);
        assertThat(violations1).isEmpty();

        // Test updating only dates
        UpdateBudgetRequest datesOnly = UpdateBudgetRequest.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .build();

        Set<ConstraintViolation<UpdateBudgetRequest>> violations2 = validator.validate(datesOnly);
        assertThat(violations2).isEmpty();
    }
}
