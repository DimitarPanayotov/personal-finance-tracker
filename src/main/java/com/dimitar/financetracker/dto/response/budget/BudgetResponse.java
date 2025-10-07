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
@Schema(description = "Budget definition details.")
public class BudgetResponse {
    @Schema(description = "Budget identifier", example = "301", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Owner user ID", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;
    @Schema(description = "Category ID this budget belongs to", example = "45", accessMode = Schema.AccessMode.READ_ONLY)
    private Long categoryId;
    @Schema(description = "Associated category name", example = "Groceries", accessMode = Schema.AccessMode.READ_ONLY)
    private String categoryName;
    @Schema(description = "Budget amount limit", example = "500.00", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal amount;
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
