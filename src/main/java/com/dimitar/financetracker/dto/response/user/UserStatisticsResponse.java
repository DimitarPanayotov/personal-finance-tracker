package com.dimitar.financetracker.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema; // added

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Aggregated financial statistics for the authenticated user.")
public class UserStatisticsResponse {
    @Schema(description = "Total income across all time (or defined aggregation scope)", example = "12500.75", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal totalIncome;
    @Schema(description = "Total expenses across all time (or defined aggregation scope)", example = "8420.10", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal totalExpenses;
    @Schema(description = "Net balance = totalIncome - totalExpenses", example = "4080.65", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal netBalance;
    @Schema(description = "Count of all transactions", example = "152", accessMode = Schema.AccessMode.READ_ONLY)
    private Long totalTransactions;
    @Schema(description = "Count of income transactions", example = "60", accessMode = Schema.AccessMode.READ_ONLY)
    private Long totalIncomeTransactions;
    @Schema(description = "Count of expense transactions", example = "92", accessMode = Schema.AccessMode.READ_ONLY)
    private Long totalExpenseTransactions;
    @Schema(description = "Average amount per income transaction", example = "208.35", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal averageIncomePerTransaction;
    @Schema(description = "Average amount per expense transaction", example = "91.52", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal averageExpensePerTransaction;
    @Schema(description = "Income for the current month", example = "2450.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal monthlyIncome;
    @Schema(description = "Expenses for the current month", example = "1800.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal monthlyExpenses;
    @Schema(description = "Net balance for the current month", example = "650.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal monthlyNetBalance;
}
