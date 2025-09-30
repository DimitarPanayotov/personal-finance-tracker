package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.dto.response.budget.BudgetUsageResponse;
import com.dimitar.financetracker.service.command.budget.CreateBudgetCommand;
import com.dimitar.financetracker.service.command.budget.UpdateBudgetCommand;
import com.dimitar.financetracker.service.command.budget.DeleteBudgetCommand;
import com.dimitar.financetracker.service.command.budget.DeactivateBudgetCommand;
import com.dimitar.financetracker.service.command.budget.ActivateBudgetCommand;
import com.dimitar.financetracker.service.query.budget.GetAllBudgetsQuery;
import com.dimitar.financetracker.service.query.budget.GetBudgetByIdQuery;
import com.dimitar.financetracker.service.query.budget.GetBudgetsByCategoryQuery;
import com.dimitar.financetracker.service.query.budget.GetBudgetUsageQuery;
import com.dimitar.financetracker.service.query.budget.GetAllBudgetsUsageQuery;
import com.dimitar.financetracker.service.query.budget.GetActiveBudgetsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final CreateBudgetCommand createBudgetCommand;
    private final GetAllBudgetsQuery getAllBudgetsQuery;
    private final UpdateBudgetCommand updateBudgetCommand;
    private final DeleteBudgetCommand deleteBudgetCommand;
    private final DeactivateBudgetCommand deactivateBudgetCommand;
    private final ActivateBudgetCommand activateBudgetCommand;
    private final GetBudgetByIdQuery getBudgetByIdQuery;
    private final GetBudgetsByCategoryQuery getBudgetsByCategoryQuery;
    private final GetBudgetUsageQuery getBudgetUsageQuery;
    private final GetAllBudgetsUsageQuery getAllBudgetsUsageQuery;
    private final GetActiveBudgetsQuery getActiveBudgetsQuery;

    public BudgetResponse createBudget(CreateBudgetRequest request) {
        return createBudgetCommand.execute(request);
    }

    public List<BudgetResponse> getAllBudgets() {
        return getAllBudgetsQuery.execute(null);
    }

    public BudgetResponse updateBudget(UpdateBudgetRequest request) {
        return updateBudgetCommand.execute(request);
    }

    public void deleteBudget(Long budgetId) {
        deleteBudgetCommand.execute(budgetId);
    }

    public BudgetResponse deactivateBudget(Long budgetId) {
        return deactivateBudgetCommand.execute(budgetId);
    }

    public BudgetResponse activateBudget(Long budgetId) {
        return activateBudgetCommand.execute(budgetId);
    }

    public BudgetResponse getBudgetById(Long budgetId) {
        return getBudgetByIdQuery.execute(budgetId);
    }

    public List<BudgetResponse> getBudgetsByCategory(Long categoryId) {
        return getBudgetsByCategoryQuery.execute(categoryId);
    }

    public BudgetUsageResponse getBudgetUsage(Long budgetId) {
        return getBudgetUsageQuery.execute(budgetId);
    }

    public List<BudgetUsageResponse> getAllBudgetsUsage() {
        return getAllBudgetsUsageQuery.execute(null);
    }

    public List<BudgetResponse> getActiveBudgets() {
        return getActiveBudgetsQuery.execute(null);
    }
}
