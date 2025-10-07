package com.dimitar.financetracker.dto.request.budget;

import com.dimitar.financetracker.model.BudgetPeriod;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.dimitar.financetracker.util.ErrorMessages.BUDGET_AMOUNT_MIN;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload to partially update an existing budget. Send only the fields you want to modify.")
public class UpdateBudgetRequest {
    @Schema(description = "ID of the budget being updated (from path, not request body)", example = "300", accessMode = Schema.AccessMode.READ_ONLY)
    private Long budgetId;

    @Schema(description = "New category ID to reassign this budget (optional)", example = "45")
    private Long categoryId;

    @DecimalMin(value = "0.01", message = BUDGET_AMOUNT_MIN)
    @Schema(description = "Updated monetary limit (optional, must be >= 0.01)", example = "650.00")
    private BigDecimal amount;

    @Schema(description = "Updated recurrence period (optional)", example = "MONTHLY")
    private BudgetPeriod period;

    @Schema(description = "Updated start date (optional, ISO-8601)", example = "2025-11-01")
    private LocalDate startDate;

    @Schema(description = "Updated end date (optional, ISO-8601)", example = "2026-04-30")
    private LocalDate endDate;
}
