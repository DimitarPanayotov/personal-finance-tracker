package com.dimitar.financetracker.service.command.budget;

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
public class DeleteBudgetCommand implements Command<Long, Void> {
    private final AuthenticationFacade authenticationFacade;
    private final BudgetRepository budgetRepository;

    @Override
    public Void execute(Long budgetId) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();

        Budget budget = budgetRepository.findByIdAndUserId(budgetId, authenticatedUserId)
            .orElseThrow(() -> new BudgetDoesNotExistException("Budget not found or access denied!"));

        budgetRepository.delete(budget);
        return null;
    }
}

