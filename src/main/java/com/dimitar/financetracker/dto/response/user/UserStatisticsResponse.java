package com.dimitar.financetracker.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatisticsResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Long totalTransactions;
    private Long totalIncomeTransactions;
    private Long totalExpenseTransactions;
    private BigDecimal averageIncomePerTransaction;
    private BigDecimal averageExpensePerTransaction;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal monthlyNetBalance;
}
