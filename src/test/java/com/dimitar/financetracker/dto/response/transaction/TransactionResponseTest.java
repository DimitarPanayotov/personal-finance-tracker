package com.dimitar.financetracker.dto.response.transaction;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionResponseTest {

    @Test
    void builderPattern_shouldWork() {
        LocalDate transactionDate = LocalDate.of(2023, 1, 1);
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 12, 0);
        BigDecimal amount = new BigDecimal("150.75");

        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(5L)
                .categoryName("Food")
                .amount(amount)
                .description("Lunch at restaurant")
                .transactionDate(transactionDate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(100L);
        assertThat(response.getCategoryId()).isEqualTo(5L);
        assertThat(response.getCategoryName()).isEqualTo("Food");
        assertThat(response.getAmount()).isEqualTo(amount);
        assertThat(response.getDescription()).isEqualTo("Lunch at restaurant");
        assertThat(response.getTransactionDate()).isEqualTo(transactionDate);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void noArgsConstructor_shouldWork() {
        TransactionResponse response = new TransactionResponse();

        assertThat(response.getId()).isNull();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getCategoryId()).isNull();
        assertThat(response.getCategoryName()).isNull();
        assertThat(response.getAmount()).isNull();
        assertThat(response.getDescription()).isNull();
        assertThat(response.getTransactionDate()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }

    @Test
    void allArgsConstructor_shouldWork() {
        LocalDate transactionDate = LocalDate.of(2023, 1, 1);
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 12, 0);
        BigDecimal amount = new BigDecimal("150.75");

        TransactionResponse response = new TransactionResponse(
                1L, 100L, 5L, "Food", amount, "Lunch", transactionDate, createdAt, updatedAt);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(100L);
        assertThat(response.getCategoryId()).isEqualTo(5L);
        assertThat(response.getCategoryName()).isEqualTo("Food");
        assertThat(response.getAmount()).isEqualTo(amount);
        assertThat(response.getDescription()).isEqualTo("Lunch");
        assertThat(response.getTransactionDate()).isEqualTo(transactionDate);
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void settersAndGetters_shouldWork() {
        TransactionResponse response = new TransactionResponse();
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
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 12, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 12, 0);
        BigDecimal amount = new BigDecimal("150.75");

        TransactionResponse response1 = TransactionResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(5L)
                .categoryName("Food")
                .amount(amount)
                .description("Lunch")
                .transactionDate(transactionDate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        TransactionResponse response2 = TransactionResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryId(5L)
                .categoryName("Food")
                .amount(amount)
                .description("Lunch")
                .transactionDate(transactionDate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void toString_shouldWork() {
        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .userId(100L)
                .categoryName("Food")
                .amount(new BigDecimal("50.00"))
                .build();

        String toString = response.toString();

        assertThat(toString).contains("TransactionResponse");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("userId=100");
        assertThat(toString).contains("categoryName=Food");
        assertThat(toString).contains("amount=50.00");
    }
}

