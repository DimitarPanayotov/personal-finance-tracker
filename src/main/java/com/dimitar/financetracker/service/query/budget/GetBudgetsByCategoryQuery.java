package com.dimitar.financetracker.service.query.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetBudgetsByCategoryQuery implements Query<Long, List<BudgetResponse>> {
    private final AuthenticationFacade authenticationFacade;
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Override
    public List<BudgetResponse> execute(Long categoryId) {
        Long userId = authenticationFacade.getAuthenticatedUserId();
        List<Budget> budgets = budgetRepository.findByUserIdAndCategoryId(userId, categoryId);
        return budgets.stream()
            .map(budgetMapper::toResponse)
            .toList();
    }
}

