package com.dimitar.financetracker.service.command.budget.input;

import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;

public record CreateBudgetCommandInput(CreateBudgetRequest request, Long userId) {
}
