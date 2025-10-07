package com.dimitar.financetracker.dto.response.budget;

import com.dimitar.financetracker.model.BudgetPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema; // added

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Budget usage metrics including consumption progress.")
public class BudgetUsageResponse {
    @Schema(description = "Budget identifier", example = "301", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Owner user ID", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;
    @Schema(description = "Category ID", example = "45", accessMode = Schema.AccessMode.READ_ONLY)
    private Long categoryId;
    @Schema(description = "Category name", example = "Groceries", accessMode = Schema.AccessMode.READ_ONLY)
    private String categoryName;

    @Schema(description = "Budget limit amount", example = "500.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal amount;
    @Schema(description = "Amount already spent within this budget period", example = "275.25", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal spent;
    @Schema(description = "Remaining amount before reaching the limit", example = "224.75", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal remaining;
    @Schema(description = "Percent used (0-100)", example = "55.05", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal percentUsed;

    @Schema(description = "Start date (inclusive)", example = "2025-11-01", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate startDate;
    @Schema(description = "End date (inclusive) if finite", example = "2026-04-30", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate endDate;
    @Schema(description = "Budget recurrence period", example = "MONTHLY", accessMode = Schema.AccessMode.READ_ONLY)
    private BudgetPeriod period;
    @Schema(description = "Whether the budget is currently active", example = "true", accessMode = Schema.AccessMode.READ_ONLY)
    private Boolean isActive;

    @Schema(description = "Creation timestamp (UTC)", example = "2025-10-01T12:34:56", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    @Schema(description = "Last update timestamp (UTC)", example = "2025-10-07T09:20:15", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
