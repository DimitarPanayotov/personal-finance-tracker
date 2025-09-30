package com.dimitar.financetracker.service.command.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class UpdateBudgetCommand implements Command<UpdateBudgetRequest, BudgetResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Override
    public BudgetResponse execute(UpdateBudgetRequest request) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();

        Budget budget = budgetRepository.findByIdAndUserId(request.getBudgetId(), authenticatedUserId)
            .orElseThrow(() -> new BudgetDoesNotExistException("Budget not found or access denied!"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.getCategoryId(), authenticatedUserId)
                .orElseThrow(() -> new CategoryDoesNotExistException(
                    "Access denied or category with this id does not exist: " + request.getCategoryId()
                ));
        }

        budgetMapper.updateEntity(budget, request, category);

        Budget savedBudget = budgetRepository.save(budget);
        return budgetMapper.toResponse(savedBudget);
    }
}

