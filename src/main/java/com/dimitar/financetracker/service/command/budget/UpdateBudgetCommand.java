package com.dimitar.financetracker.service.command.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.exception.budget.OverlappingBudgetException;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

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

        Long effectiveCategoryId = category != null ? category.getId() :
                                   (budget.getCategory() != null ? budget.getCategory().getId() : null);
        LocalDate effectiveStartDate = request.getStartDate() != null ? request.getStartDate() : budget.getStartDate();
        LocalDate effectiveEndDate = request.getEndDate() != null ? request.getEndDate() : budget.getEndDate();

        if (budget.getIsActive() && effectiveCategoryId != null) {
            validateNoOverlappingBudgets(
                authenticatedUserId,
                effectiveCategoryId,
                effectiveStartDate,
                effectiveEndDate,
                budget.getId()
            );
        }

        budgetMapper.updateEntity(budget, request, category);

        Budget savedBudget = budgetRepository.save(budget);
        return budgetMapper.toResponse(savedBudget);
    }

    private void validateNoOverlappingBudgets(Long userId, Long categoryId,
                                              LocalDate startDate,
                                              LocalDate endDate,
                                              Long excludeBudgetId) {
        List<Budget> overlapping = budgetRepository.findOverlappingActiveBudgets(
            userId, categoryId, startDate, endDate, excludeBudgetId
        );

        if (!overlapping.isEmpty()) {
            Budget existing = overlapping.get(0);
            throw new OverlappingBudgetException(
                String.format(
                    "Cannot update budget: an active budget already exists for category '%s' " +
                    "from %s to %s. Please deactivate the existing budget first or choose a different date range.",
                    existing.getCategory().getName(),
                    existing.getStartDate(),
                    existing.getEndDate()
                )
            );
        }
    }
}
