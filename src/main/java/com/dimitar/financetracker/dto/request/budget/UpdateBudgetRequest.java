package com.dimitar.financetracker.dto.request.budget;

import com.dimitar.financetracker.model.BudgetPeriod;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.dimitar.financetracker.util.ErrorMessages.BUDGET_AMOUNT_MIN;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBudgetRequest {
    private Long budgetId;

    private Long categoryId;

    @DecimalMin(value = "0.01", message = BUDGET_AMOUNT_MIN)
    private BigDecimal amount;

    private BudgetPeriod period;

    private LocalDate startDate;

    private LocalDate endDate;
}
