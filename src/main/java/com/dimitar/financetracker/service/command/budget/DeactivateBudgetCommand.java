package com.dimitar.financetracker.service.command.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class DeactivateBudgetCommand implements Command<Long, BudgetResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Override
    public BudgetResponse execute(Long budgetId) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();

        Budget budget = budgetRepository.findByIdAndUserId(budgetId, authenticatedUserId)
            .orElseThrow(() -> new BudgetDoesNotExistException("Budget not found or access denied!"));

        Boolean active = budget.getIsActive();
        if (active == null || active) {
            budget.setIsActive(false);
            budget = budgetRepository.save(budget);
        }

        return budgetMapper.toResponse(budget);
    }
}
