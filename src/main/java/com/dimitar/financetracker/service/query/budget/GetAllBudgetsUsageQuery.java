package com.dimitar.financetracker.service.query.budget;

import com.dimitar.financetracker.dto.response.budget.BudgetUsageResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetAllBudgetsUsageQuery implements Query<Void, List<BudgetUsageResponse>> {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final AuthenticationFacade authenticationFacade;
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public List<BudgetUsageResponse> execute(Void input) {
        Long userId = authenticationFacade.getAuthenticatedUserId();
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream()
            .map(budget -> toUsageResponse(budget, userId))
            .collect(Collectors.toList());
    }

    private BudgetUsageResponse toUsageResponse(Budget budget, Long userId) {
        BigDecimal spent = transactionRepository.sumAmountByUserAndCategoryAndDateRange(
            userId,
            budget.getCategory().getId(),
            budget.getStartDate(),
            budget.getEndDate()
        );
        if (spent == null) {
            spent = BigDecimal.ZERO;
        }
        BigDecimal amount = budget.getAmount() == null ? BigDecimal.ZERO : budget.getAmount();
        BigDecimal remaining = amount.subtract(spent);
        BigDecimal percentUsed = BigDecimal.ZERO;
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            percentUsed = spent.multiply(ONE_HUNDRED).divide(amount, 2, RoundingMode.HALF_UP);
        }
        return BudgetUsageResponse.builder()
            .id(budget.getId())
            .userId(budget.getUser().getId())
            .categoryId(budget.getCategory().getId())
            .categoryName(budget.getCategory().getName())
            .amount(amount)
            .spent(spent)
            .remaining(remaining)
            .percentUsed(percentUsed)
            .startDate(budget.getStartDate())
            .endDate(budget.getEndDate())
            .period(budget.getPeriod())
            .isActive(budget.getIsActive())
            .createdAt(budget.getCreatedAt())
            .updatedAt(budget.getUpdatedAt())
            .build();
    }
}
