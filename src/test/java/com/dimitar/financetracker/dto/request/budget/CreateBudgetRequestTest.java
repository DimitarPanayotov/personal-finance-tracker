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

class CreateBudgetRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid CreateBudgetRequest with all required fields")
    void shouldCreateValidCreateBudgetRequest() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.getCategoryId()).isEqualTo(1L);
        assertThat(request.getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(request.getPeriod()).isEqualTo(BudgetPeriod.MONTHLY);
        assertThat(request.getStartDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Should create valid CreateBudgetRequest with end date")
    void shouldCreateValidCreateBudgetRequestWithEndDate() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);

        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("250.50"))
                .period(BudgetPeriod.CUSTOM)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // When
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
        assertThat(request.getEndDate()).isEqualTo(endDate);
    }

    @Test
    @DisplayName("Should fail validation when categoryId is null")
    void shouldFailValidationWhenCategoryIdIsNull() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(null)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Category is required");
    }

    @Test
    @DisplayName("Should fail validation when amount is null")
    void shouldFailValidationWhenAmountIsNull() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(null)
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Amount is required");
    }

    @Test
    @DisplayName("Should fail validation when amount is zero")
    void shouldFailValidationWhenAmountIsZero() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(BigDecimal.ZERO)
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Budget amount must be at least 0.01");
    }

    @Test
    @DisplayName("Should fail validation when amount is negative")
    void shouldFailValidationWhenAmountIsNegative() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("-10.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Budget amount must be at least 0.01");
    }

    @Test
    @DisplayName("Should fail validation when period is null")
    void shouldFailValidationWhenPeriodIsNull() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.00"))
                .period(null)
                .startDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Budget period is required");
    }

    @Test
    @DisplayName("Should fail validation when startDate is null")
    void shouldFailValidationWhenStartDateIsNull() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(null)
                .build();

        // When
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Start date is required");
    }

    @Test
    @DisplayName("Should fail validation when multiple fields are invalid")
    void shouldFailValidationWhenMultipleFieldsAreInvalid() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(null)
                .amount(null)
                .period(null)
                .startDate(null)
                .build();

        // When
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(4);
    }

    @Test
    @DisplayName("Should accept minimum valid amount")
    void shouldAcceptMinimumValidAmount() {
        // Given
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("0.01"))
                .period(BudgetPeriod.WEEKLY)
                .startDate(LocalDate.now())
                .build();

        // When
        Set<ConstraintViolation<CreateBudgetRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should test builder pattern functionality")
    void shouldTestBuilderPattern() {
        // Given & When
        CreateBudgetRequest request = CreateBudgetRequest.builder()
                .categoryId(5L)
                .amount(new BigDecimal("1500.75"))
                .period(BudgetPeriod.YEARLY)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .build();

        // Then
        assertThat(request.getCategoryId()).isEqualTo(5L);
        assertThat(request.getAmount()).isEqualTo(new BigDecimal("1500.75"));
        assertThat(request.getPeriod()).isEqualTo(BudgetPeriod.YEARLY);
        assertThat(request.getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(request.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 31));
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsAndHashCode() {
        // Given
        CreateBudgetRequest request1 = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.now())
                .build();

        CreateBudgetRequest request2 = CreateBudgetRequest.builder()
                .categoryId(1L)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.now())
                .build();

        CreateBudgetRequest request3 = CreateBudgetRequest.builder()
                .categoryId(2L)
                .amount(new BigDecimal("200.00"))
                .period(BudgetPeriod.WEEKLY)
                .startDate(LocalDate.now())
                .build();

        // Then
        assertThat(request1).isEqualTo(request2);
        assertThat(request1).isNotEqualTo(request3);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }
}
