package com.dimitar.financetracker.service.command.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class CreateBudgetCommand implements Command<CreateBudgetRequest, BudgetResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetMapper budgetMapper;

    @Override
    public BudgetResponse execute(CreateBudgetRequest request) {
        User user = authenticationFacade.getAuthenticatedUser();
//        User user = userRepository.findById(input.userId())
//            .orElseThrow(() -> new UserDoesNotExistException("User with this id does not exist: " + input.userId()));

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new CategoryDoesNotExistException("Category with this id does not exist: " + request.getCategoryId()));

        Budget budget = budgetMapper.toEntity(request, user, category);
        Budget savedBudget = budgetRepository.save(budget);

        return budgetMapper.toResponse(savedBudget);
    }
}
