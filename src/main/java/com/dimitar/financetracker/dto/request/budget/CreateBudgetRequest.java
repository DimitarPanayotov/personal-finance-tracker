package com.dimitar.financetracker.dto.request.budget;

import com.dimitar.financetracker.model.BudgetPeriod;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Payload to create a new budget tied to a category and time period.")
public class CreateBudgetRequest {
    @NotNull(message = CATEGORY_REQUIRED)
    @Schema(description = "ID of the category this budget applies to", example = "45")
    private Long categoryId;

    @NotNull(message = AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = BUDGET_AMOUNT_MIN)
    @Schema(description = "Monetary budget limit (must be >= 0.01)", example = "500.00")
    private BigDecimal amount;

    @NotNull(message = BUDGET_PERIOD_REQUIRED)
    @Schema(description = "Budget recurrence period", example = "MONTHLY")
    private BudgetPeriod period;

    @NotNull(message = START_DATE_REQUIRED)
    @Schema(description = "Start date (inclusive) for this budget (ISO-8601)", example = "2025-11-01")
    private LocalDate startDate;

    @Schema(description = "Optional end date (inclusive). If omitted, derived from period or considered open-ended.", example = "2026-04-30")
    private LocalDate endDate;
}
