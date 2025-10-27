package com.dimitar.financetracker.service.command.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.exception.budget.OverlappingBudgetException;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class ActivateBudgetCommand implements Command<Long, BudgetResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Override
    public BudgetResponse execute(Long budgetId) {
        Long userId = authenticationFacade.getAuthenticatedUserId();

        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
            .orElseThrow(() -> new BudgetDoesNotExistException("Budget not found or access denied!"));

        Boolean active = budget.getIsActive();
        if (active == null || !active) {
            // Validate no overlapping active budgets before activating
            if (budget.getCategory() != null) {
                validateNoOverlappingBudgets(
                    userId,
                    budget.getCategory().getId(),
                    budget.getStartDate(),
                    budget.getEndDate(),
                    budget.getId()
                );
            }

            budget.setIsActive(true);
            budget = budgetRepository.save(budget);
        }

        return budgetMapper.toResponse(budget);
    }

    private void validateNoOverlappingBudgets(Long userId, Long categoryId,
                                              java.time.LocalDate startDate,
                                              java.time.LocalDate endDate,
                                              Long excludeBudgetId) {
        List<Budget> overlapping = budgetRepository.findOverlappingActiveBudgets(
            userId, categoryId, startDate, endDate, excludeBudgetId
        );

        if (!overlapping.isEmpty()) {
            Budget existing = overlapping.get(0);
            throw new OverlappingBudgetException(
                String.format(
                    "Cannot activate budget: an active budget already exists for category '%s' " +
                    "from %s to %s. Please deactivate the existing budget first.",
                    existing.getCategory().getName(),
                    existing.getStartDate(),
                    existing.getEndDate()
                )
            );
        }
    }
}
