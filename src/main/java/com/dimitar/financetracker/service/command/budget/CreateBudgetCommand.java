package com.dimitar.financetracker.service.command.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.exception.budget.OverlappingBudgetException;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class CreateBudgetCommand implements Command<CreateBudgetRequest, BudgetResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Override
    public BudgetResponse execute(CreateBudgetRequest request) {
        User user = authenticationFacade.getAuthenticatedUser();

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
            .orElseThrow(() -> new CategoryDoesNotExistException("Access denied or category with this id does not exist: " + request.getCategoryId()));

        Budget budget = budgetMapper.toEntity(request, user, category);

        validateNoOverlappingBudgets(user.getId(), category.getId(), budget.getStartDate(), budget.getEndDate());

        Budget savedBudget = budgetRepository.save(budget);

        return budgetMapper.toResponse(savedBudget);
    }

    private void validateNoOverlappingBudgets(Long userId, Long categoryId,
                                              java.time.LocalDate startDate,
                                              java.time.LocalDate endDate) {
        List<Budget> overlapping = budgetRepository.findOverlappingActiveBudgets(
            userId, categoryId, startDate, endDate
        );

        if (!overlapping.isEmpty()) {
            Budget existing = overlapping.get(0);
            throw new OverlappingBudgetException(
                String.format(
                    "Cannot create budget: an active budget already exists for category '%s' " +
                    "from %s to %s. Please deactivate the existing budget first or choose a different date range.",
                    existing.getCategory().getName(),
                    existing.getStartDate(),
                    existing.getEndDate()
                )
            );
        }
    }
}
