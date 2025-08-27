package com.dimitar.financetracker.dto.response.budget;

import com.dimitar.financetracker.model.BudgetPeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetSummaryResponseTest {

    @Test
    @DisplayName("Should create BudgetSummaryResponse with all fields using builder")
    void shouldCreateBudgetSummaryResponseWithAllFields() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 2, 29);

        // When
        BudgetSummaryResponse response = BudgetSummaryResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(5L)
                .categoryName("Shopping")
                .amount(new BigDecimal("750.00"))
                .startDate(startDate)
                .endDate(endDate)
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(100L);
        assertThat(response.getCategoryId()).isEqualTo(5L);
        assertThat(response.getCategoryName()).isEqualTo("Shopping");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("750.00"));
        assertThat(response.getStartDate()).isEqualTo(startDate);
        assertThat(response.getEndDate()).isEqualTo(endDate);
        assertThat(response.getPeriod()).isEqualTo(BudgetPeriod.MONTHLY);
        assertThat(response.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should create BudgetSummaryResponse with minimal fields")
    void shouldCreateBudgetSummaryResponseWithMinimalFields() {
        // When
        BudgetSummaryResponse response = BudgetSummaryResponse.builder()
                .id(3L)
                .amount(new BigDecimal("50.25"))
                .period(BudgetPeriod.WEEKLY)
                .isActive(false)
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("50.25"));
        assertThat(response.getPeriod()).isEqualTo(BudgetPeriod.WEEKLY);
        assertThat(response.getIsActive()).isFalse();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getCategoryId()).isNull();
        assertThat(response.getCategoryName()).isNull();
        assertThat(response.getStartDate()).isNull();
        assertThat(response.getEndDate()).isNull();
    }

    @Test
    @DisplayName("Should create BudgetSummaryResponse using no-args constructor")
    void shouldCreateBudgetSummaryResponseUsingNoArgsConstructor() {
        // When
        BudgetSummaryResponse response = new BudgetSummaryResponse();

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
    }

    @Test
    @DisplayName("Should create BudgetSummaryResponse using all-args constructor")
    void shouldCreateBudgetSummaryResponseUsingAllArgsConstructor() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 4, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);

        // When
        BudgetSummaryResponse response = new BudgetSummaryResponse(
                7L,
                300L,
                20L,
                "Utilities",
                new BigDecimal("450.50"),
                startDate,
                endDate,
                BudgetPeriod.QUARTERLY,
                true
        );

        // Then
        assertThat(response.getId()).isEqualTo(7L);
        assertThat(response.getUserId()).isEqualTo(300L);
        assertThat(response.getCategoryId()).isEqualTo(20L);
        assertThat(response.getCategoryName()).isEqualTo("Utilities");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("450.50"));
        assertThat(response.getStartDate()).isEqualTo(startDate);
        assertThat(response.getEndDate()).isEqualTo(endDate);
        assertThat(response.getPeriod()).isEqualTo(BudgetPeriod.QUARTERLY);
        assertThat(response.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should test equals and hashCode for identical objects")
    void shouldTestEqualsAndHashCodeForIdenticalObjects() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        BudgetSummaryResponse response1 = BudgetSummaryResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(10L)
                .categoryName("Travel")
                .amount(new BigDecimal("2000.00"))
                .startDate(startDate)
                .endDate(endDate)
                .period(BudgetPeriod.YEARLY)
                .isActive(true)
                .build();

        BudgetSummaryResponse response2 = BudgetSummaryResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(10L)
                .categoryName("Travel")
                .amount(new BigDecimal("2000.00"))
                .startDate(startDate)
                .endDate(endDate)
                .period(BudgetPeriod.YEARLY)
                .isActive(true)
                .build();

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    @DisplayName("Should test equals for different objects")
    void shouldTestEqualsForDifferentObjects() {
        // Given
        BudgetSummaryResponse response1 = BudgetSummaryResponse.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .period(BudgetPeriod.MONTHLY)
                .build();

        BudgetSummaryResponse response2 = BudgetSummaryResponse.builder()
                .id(2L)
                .amount(new BigDecimal("200.00"))
                .period(BudgetPeriod.WEEKLY)
                .build();

        // Then
        assertThat(response1).isNotEqualTo(response2);
        assertThat(response1).isNotEqualTo(null);
        assertThat(response1).isNotEqualTo("not a BudgetSummaryResponse");
    }

    @Test
    @DisplayName("Should test toString method")
    void shouldTestToStringMethod() {
        // Given
        BudgetSummaryResponse response = BudgetSummaryResponse.builder()
                .id(5L)
                .userId(250L)
                .categoryName("Education")
                .amount(new BigDecimal("800.00"))
                .period(BudgetPeriod.CUSTOM)
                .isActive(false)
                .build();

        // When
        String toString = response.toString();

        // Then
        assertThat(toString).contains("BudgetSummaryResponse");
        assertThat(toString).contains("id=5");
        assertThat(toString).contains("userId=250");
        assertThat(toString).contains("categoryName=Education");
        assertThat(toString).contains("amount=800.00");
        assertThat(toString).contains("period=CUSTOM");
        assertThat(toString).contains("isActive=false");
    }

    @Test
    @DisplayName("Should test setters and getters")
    void shouldTestSettersAndGetters() {
        // Given
        BudgetSummaryResponse response = new BudgetSummaryResponse();
        LocalDate today = LocalDate.now();

        // When
        response.setId(15L);
        response.setUserId(600L);
        response.setCategoryId(35L);
        response.setCategoryName("Personal Care");
        response.setAmount(new BigDecimal("120.75"));
        response.setStartDate(today);
        response.setEndDate(today.plusDays(14));
        response.setPeriod(BudgetPeriod.CUSTOM);
        response.setIsActive(true);

        // Then
        assertThat(response.getId()).isEqualTo(15L);
        assertThat(response.getUserId()).isEqualTo(600L);
        assertThat(response.getCategoryId()).isEqualTo(35L);
        assertThat(response.getCategoryName()).isEqualTo("Personal Care");
        assertThat(response.getAmount()).isEqualTo(new BigDecimal("120.75"));
        assertThat(response.getStartDate()).isEqualTo(today);
        assertThat(response.getEndDate()).isEqualTo(today.plusDays(14));
        assertThat(response.getPeriod()).isEqualTo(BudgetPeriod.CUSTOM);
        assertThat(response.getIsActive()).isTrue();
    }

    @Test
    @DisplayName("Should handle all budget periods")
    void shouldHandleAllBudgetPeriods() {
        for (BudgetPeriod period : BudgetPeriod.values()) {
            // When
            BudgetSummaryResponse response = BudgetSummaryResponse.builder()
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
        BudgetSummaryResponse response = BudgetSummaryResponse.builder()
                .id(null)
                .userId(null)
                .categoryId(null)
                .categoryName(null)
                .amount(null)
                .startDate(null)
                .endDate(null)
                .period(null)
                .isActive(null)
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
    }

    @Test
    @DisplayName("Should compare with BudgetResponse structure")
    void shouldCompareWithBudgetResponseStructure() {
        // Given - BudgetSummaryResponse should have same fields as BudgetResponse except timestamps
        BudgetSummaryResponse summaryResponse = BudgetSummaryResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(10L)
                .categoryName("Test Category")
                .amount(new BigDecimal("100.00"))
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();

        // Then - All core fields should be present (excluding createdAt/updatedAt)
        assertThat(summaryResponse.getId()).isNotNull();
        assertThat(summaryResponse.getUserId()).isNotNull();
        assertThat(summaryResponse.getCategoryId()).isNotNull();
        assertThat(summaryResponse.getCategoryName()).isNotNull();
        assertThat(summaryResponse.getAmount()).isNotNull();
        assertThat(summaryResponse.getStartDate()).isNotNull();
        assertThat(summaryResponse.getEndDate()).isNotNull();
        assertThat(summaryResponse.getPeriod()).isNotNull();
        assertThat(summaryResponse.getIsActive()).isNotNull();
    }
}
