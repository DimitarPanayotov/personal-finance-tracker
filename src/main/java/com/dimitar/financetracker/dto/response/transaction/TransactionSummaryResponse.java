package com.dimitar.financetracker.dto.response.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionSummaryResponse {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private String description;
    private LocalDate transactionDate;
}
