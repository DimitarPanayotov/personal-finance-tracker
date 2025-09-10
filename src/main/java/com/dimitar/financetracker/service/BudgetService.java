package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.service.command.budget.CreateBudgetCommand;
import com.dimitar.financetracker.service.query.budget.GetAllBudgetsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final CreateBudgetCommand createBudgetCommand;
    private final GetAllBudgetsQuery getAllBudgetsQuery;

    public BudgetResponse createBudget(CreateBudgetRequest request) {
        return createBudgetCommand.execute(request);
    }

    public List<BudgetResponse> getAllBudgets() {
        return getAllBudgetsQuery.execute(null);
    }
}
