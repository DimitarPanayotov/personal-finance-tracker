package com.dimitar.financetracker.dto.response.budget;

import com.dimitar.financetracker.model.BudgetPeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetResponseTest {

    @Test
    @DisplayName("Should create BudgetResponse with all fields using builder")
    void shouldCreateBudgetResponseWithAllFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        // When
        BudgetResponse response = BudgetResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(10L)
                .categoryName("Food & Dining")
                .amount(new BigDecimal("500.00"))
                .startDate(startDate)
                .endDate(endDate)
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(100L);
        assertThat(response.getCategoryId()).isEqualTo(10L);
        assertThat(response.getCategoryName()).isEqualTo("Food & Dining");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(response.getStartDate()).isEqualTo(startDate);
        assertThat(response.getEndDate()).isEqualTo(endDate);
        assertThat(response.getPeriod()).isEqualTo(BudgetPeriod.MONTHLY);
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should create BudgetResponse with minimal fields")
    void shouldCreateBudgetResponseWithMinimalFields() {
        // When
        BudgetResponse response = BudgetResponse.builder()
                .id(2L)
                .amount(new BigDecimal("100.50"))
                .period(BudgetPeriod.WEEKLY)
                .isActive(false)
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("100.50"));
        assertThat(response.getPeriod()).isEqualTo(BudgetPeriod.WEEKLY);
        assertThat(response.getIsActive()).isFalse();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getCategoryId()).isNull();
        assertThat(response.getCategoryName()).isNull();
        assertThat(response.getStartDate()).isNull();
        assertThat(response.getEndDate()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should create BudgetResponse using no-args constructor")
    void shouldCreateBudgetResponseUsingNoArgsConstructor() {
        // When
        BudgetResponse response = new BudgetResponse();

        // Then
        assertThat(response.getId()).isNull();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getCategoryId()).isNull();
        assertThat(response.getCategoryName()).isNull();
        assertThat(response.getAmount()).isNull();
        assertThat(response.getStartDate()).isNull();
        assertThat(response.getEndDate()).isNull();
        assertThat(response.getPeriod()).isNull();
        assertThat(response.getIsActive()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should create BudgetResponse using all-args constructor")
    void shouldCreateBudgetResponseUsingAllArgsConstructor() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 15, 15, 30);
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        // When
        BudgetResponse response = new BudgetResponse(
                5L,
                200L,
                15L,
                "Transportation",
                new BigDecimal("300.75"),
                startDate,
                endDate,
                BudgetPeriod.QUARTERLY,
                true,
                createdAt,
                updatedAt
        );

        // Then
        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getUserId()).isEqualTo(200L);
        assertThat(response.getCategoryId()).isEqualTo(15L);
        assertThat(response.getCategoryName()).isEqualTo("Transportation");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("300.75"));
        assertThat(response.getStartDate()).isEqualTo(startDate);
        assertThat(response.getEndDate()).isEqualTo(endDate);
        assertThat(response.getPeriod()).isEqualTo(BudgetPeriod.QUARTERLY);
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("Should test equals and hashCode for identical objects")
    void shouldTestEqualsAndHashCodeForIdenticalObjects() {
        // Given
        LocalDateTime timestamp = LocalDateTime.now();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        BudgetResponse response1 = BudgetResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(10L)
                .categoryName("Entertainment")
                .amount(new BigDecimal("200.00"))
                .startDate(startDate)
                .endDate(endDate)
                .period(BudgetPeriod.YEARLY)
                .isActive(true)
                .createdAt(timestamp)
                .updatedAt(timestamp)
                .build();

        BudgetResponse response2 = BudgetResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(10L)
                .categoryName("Entertainment")
                .amount(new BigDecimal("200.00"))
                .startDate(startDate)
                .endDate(endDate)
                .period(BudgetPeriod.YEARLY)
                .isActive(true)
                .createdAt(timestamp)
                .updatedAt(timestamp)
                .build();

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Should test equals for different objects")
    void shouldTestEqualsForDifferentObjects() {
        // Given
        BudgetResponse response1 = BudgetResponse.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .build();

        BudgetResponse response2 = BudgetResponse.builder()
                .id(2L)
                .amount(new BigDecimal("200.00"))
                .period(BudgetPeriod.WEEKLY)
                .build();

        // Then
        assertThat(response1).isNotEqualTo(response2);
        assertThat(response1).isNotEqualTo(null);
        assertThat(response1).isNotEqualTo("not a BudgetResponse");
    }

    @Test
    @DisplayName("Should test toString method")
    void shouldTestToStringMethod() {
        // Given
        BudgetResponse response = BudgetResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryName("Groceries")
                .amount(new BigDecimal("400.00"))
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();

        // When
        String toString = response.toString();

        // Then
        assertThat(toString).contains("BudgetResponse");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("userId=100");
        assertThat(toString).contains("categoryName=Groceries");
        assertThat(toString).contains("amount=400.00");
        assertThat(toString).contains("period=MONTHLY");
        assertThat(toString).contains("isActive=true");
    }

    @Test
    @DisplayName("Should test setters and getters")
    void shouldTestSettersAndGetters() {
        // Given
        BudgetResponse response = new BudgetResponse();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        // When
        response.setId(10L);
        response.setUserId(500L);
        response.setCategoryId(25L);
        response.setCategoryName("Health & Fitness");
        response.setAmount(new BigDecimal("150.25"));
        response.setStartDate(today);
        response.setEndDate(today.plusDays(7));
        response.setPeriod(BudgetPeriod.CUSTOM);
        response.setIsActive(false);
        response.setCreatedAt(now);
        response.setUpdatedAt(now);

        // Then
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getUserId()).isEqualTo(500L);
        assertThat(response.getCategoryId()).isEqualTo(25L);
        assertThat(response.getCategoryName()).isEqualTo("Health & Fitness");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("150.25"));
        assertThat(response.getStartDate()).isEqualTo(today);
        assertThat(response.getEndDate()).isEqualTo(today.plusDays(7));
        assertThat(response.getPeriod()).isEqualTo(BudgetPeriod.CUSTOM);
        assertThat(response.getIsActive()).isFalse();
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should handle all budget periods")
    void shouldHandleAllBudgetPeriods() {
        for (BudgetPeriod period : BudgetPeriod.values()) {
            // When
            BudgetResponse response = BudgetResponse.builder()
                    .id(1L)
                    .period(period)
                    .build();

            // Then
            assertThat(response.getPeriod()).isEqualTo(period);
        }
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValuesGracefully() {
        // When
        BudgetResponse response = BudgetResponse.builder()
                .id(null)
                .userId(null)
                .categoryId(null)
                .categoryName(null)
                .amount(null)
                .startDate(null)
                .endDate(null)
                .period(null)
                .isActive(null)
                .createdAt(null)
                .updatedAt(null)
                .build();

        // Then
        assertThat(response.getId()).isNull();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getCategoryId()).isNull();
        assertThat(response.getCategoryName()).isNull();
        assertThat(response.getAmount()).isNull();
        assertThat(response.getStartDate()).isNull();
        assertThat(response.getEndDate()).isNull();
        assertThat(response.getPeriod()).isNull();
        assertThat(response.getIsActive()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }
}
