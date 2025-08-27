package com.dimitar.financetracker.dto.request.budget;

import com.dimitar.financetracker.model.BudgetPeriod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.dimitar.financetracker.util.ErrorMessages.AMOUNT_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.BUDGET_AMOUNT_MIN;
import static com.dimitar.financetracker.util.ErrorMessages.BUDGET_PERIOD_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.START_DATE_REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBudgetRequest {
    @NotNull(message = CATEGORY_REQUIRED)
    private Long categoryId;

    @NotNull(message = AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = BUDGET_AMOUNT_MIN)
    private BigDecimal amount;

    @NotNull(message = BUDGET_PERIOD_REQUIRED)
    private BudgetPeriod period;

    @NotNull(message = START_DATE_REQUIRED)
    private LocalDate startDate;

    private LocalDate endDate;
}
