package com.dimitar.financetracker.service.query.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetBudgetByIdQuery implements Query<Long, BudgetResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Override
    public BudgetResponse execute(Long budgetId) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, authenticatedUserId)
            .orElseThrow(() -> new BudgetDoesNotExistException("Budget not found or access denied!"));
        return budgetMapper.toResponse(budget);
    }
}

