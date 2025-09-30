package com.dimitar.financetracker.dto.response.budget;

import com.dimitar.financetracker.model.BudgetPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetUsageResponse {
    private Long id;
    private Long userId;
    private Long categoryId;
    private String categoryName;

    private BigDecimal amount;
    private BigDecimal spent;
    private BigDecimal remaining;
    private BigDecimal percentUsed;

    private LocalDate startDate;
    private LocalDate endDate;
    private BudgetPeriod period;
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

