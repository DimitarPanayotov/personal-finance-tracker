package com.dimitar.financetracker.dto.response.transaction;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionSummaryResponseTest {

    @Test
    void builderPattern_shouldWork() {
        LocalDate transactionDate = LocalDate.of(2023, 1, 1);
        BigDecimal amount = new BigDecimal("150.75");

        TransactionSummaryResponse response = TransactionSummaryResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(5L)
                .categoryName("Food")
                .amount(amount)
                .description("Lunch at restaurant")
                .transactionDate(transactionDate)
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(100L);
        assertThat(response.getCategoryId()).isEqualTo(5L);
        assertThat(response.getCategoryName()).isEqualTo("Food");
        assertThat(response.getAmount()).isEqualTo(amount);
        assertThat(response.getDescription()).isEqualTo("Lunch at restaurant");
        assertThat(response.getTransactionDate()).isEqualTo(transactionDate);
    }

    @Test
    void noArgsConstructor_shouldWork() {
        TransactionSummaryResponse response = new TransactionSummaryResponse();

        assertThat(response.getId()).isNull();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getCategoryId()).isNull();
        assertThat(response.getCategoryName()).isNull();
        assertThat(response.getAmount()).isNull();
        assertThat(response.getDescription()).isNull();
        assertThat(response.getTransactionDate()).isNull();
    }

    @Test
    void allArgsConstructor_shouldWork() {
        LocalDate transactionDate = LocalDate.of(2023, 1, 1);
        BigDecimal amount = new BigDecimal("150.75");

        TransactionSummaryResponse response = new TransactionSummaryResponse(
                1L, 100L, 5L, "Food", amount, "Lunch", transactionDate);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(100L);
        assertThat(response.getCategoryId()).isEqualTo(5L);
        assertThat(response.getCategoryName()).isEqualTo("Food");
        assertThat(response.getAmount()).isEqualTo(amount);
        assertThat(response.getDescription()).isEqualTo("Lunch");
        assertThat(response.getTransactionDate()).isEqualTo(transactionDate);
    }

    @Test
    void settersAndGetters_shouldWork() {
        TransactionSummaryResponse response = new TransactionSummaryResponse();
        LocalDate transactionDate = LocalDate.of(2023, 1, 1);
        BigDecimal amount = new BigDecimal("200.00");

        response.setId(2L);
        response.setUserId(200L);
        response.setCategoryId(10L);
        response.setCategoryName("Entertainment");
        response.setAmount(amount);
        response.setDescription("Movie tickets");
        response.setTransactionDate(transactionDate);

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getUserId()).isEqualTo(200L);
        assertThat(response.getCategoryId()).isEqualTo(10L);
        assertThat(response.getCategoryName()).isEqualTo("Entertainment");
        assertThat(response.getAmount()).isEqualTo(amount);
        assertThat(response.getDescription()).isEqualTo("Movie tickets");
        assertThat(response.getTransactionDate()).isEqualTo(transactionDate);
    }

    @Test
    void equalsAndHashCode_shouldWork() {
        LocalDate transactionDate = LocalDate.of(2023, 1, 1);
        BigDecimal amount = new BigDecimal("150.75");

        TransactionSummaryResponse response1 = TransactionSummaryResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(5L)
                .categoryName("Food")
                .amount(amount)
                .description("Lunch")
                .transactionDate(transactionDate)
                .build();

        TransactionSummaryResponse response2 = TransactionSummaryResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(5L)
                .categoryName("Food")
                .amount(amount)
                .description("Lunch")
                .transactionDate(transactionDate)
                .build();

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void toString_shouldWork() {
        TransactionSummaryResponse response = TransactionSummaryResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryName("Food")
                .amount(new BigDecimal("50.00"))
                .build();

        String toString = response.toString();

        assertThat(toString).contains("TransactionSummaryResponse");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("userId=100");
        assertThat(toString).contains("categoryName=Food");
        assertThat(toString).contains("amount=50.00");
    }

    @Test
    void equalsWithNullFields_shouldWork() {
        TransactionSummaryResponse response1 = new TransactionSummaryResponse();
        TransactionSummaryResponse response2 = new TransactionSummaryResponse();

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void builderWithNullValues_shouldWork() {
        TransactionSummaryResponse response = TransactionSummaryResponse.builder()
                .id(1L)
                .userId(null)
                .categoryId(null)
                .categoryName(null)
                .amount(null)
                .description(null)
                .transactionDate(null)
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isNull();
        assertThat(response.getCategoryId()).isNull();
        assertThat(response.getCategoryName()).isNull();
        assertThat(response.getAmount()).isNull();
        assertThat(response.getDescription()).isNull();
        assertThat(response.getTransactionDate()).isNull();
    }
}

