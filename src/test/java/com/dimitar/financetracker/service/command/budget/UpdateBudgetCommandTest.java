package com.dimitar.financetracker.service.command.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.model.BudgetPeriod;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBudgetCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private BudgetMapper budgetMapper;

    private UpdateBudgetCommand command;

    @BeforeEach
    void setUp() { command = new UpdateBudgetCommand(authenticationFacade, categoryRepository, budgetRepository, budgetMapper); }

    @Test
    void execute_updatesBudget_whenFound_andCategoryOwned() {
        Long userId = 1L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Long budgetId = 50L; Budget budget = Budget.builder().id(budgetId).build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(budget));

        Long categoryId = 10L; Category category = Category.builder().id(categoryId).build();
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(category));

        UpdateBudgetRequest req = UpdateBudgetRequest.builder()
                .budgetId(budgetId)
                .categoryId(categoryId)
                .amount(new BigDecimal("250.00"))
                .period(BudgetPeriod.MONTHLY)
                .startDate(LocalDate.of(2025,1,1))
                .endDate(LocalDate.of(2025,1,31))
                .build();

        when(budgetRepository.save(budget)).thenReturn(budget);
        BudgetResponse expected = BudgetResponse.builder().id(budgetId).categoryId(categoryId).amount(new BigDecimal("250.00")).period(BudgetPeriod.MONTHLY).build();
        when(budgetMapper.toResponse(budget)).thenReturn(expected);

        BudgetResponse result = command.execute(req);

        verify(budgetMapper).updateEntity(budget, req, category);
        verify(budgetRepository).save(budget);
        assertEquals(expected, result);
    }

    @Test
    void execute_throwsWhenBudgetNotFound() {
        Long userId = 1L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(budgetRepository.findByIdAndUserId(999L, userId)).thenReturn(Optional.empty());

        UpdateBudgetRequest req = UpdateBudgetRequest.builder().budgetId(999L).build();
        assertThrows(BudgetDoesNotExistException.class, () -> command.execute(req));
        verifyNoInteractions(categoryRepository, budgetMapper);
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void execute_throwsWhenCategoryNotOwnedOrMissing() {
        Long userId = 1L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Long budgetId = 50L; Budget budget = Budget.builder().id(budgetId).build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(budget));

        when(categoryRepository.findByIdAndUserId(123L, userId)).thenReturn(Optional.empty());

        UpdateBudgetRequest req = UpdateBudgetRequest.builder().budgetId(budgetId).categoryId(123L).build();
        assertThrows(CategoryDoesNotExistException.class, () -> command.execute(req));
        verify(budgetRepository, never()).save(any());
        verify(budgetMapper, never()).updateEntity(any(), any(), any());
    }

    @Test
    void execute_updatesWithoutChangingCategory_whenCategoryIdNull() {
        Long userId = 1L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Long budgetId = 50L; Budget budget = Budget.builder().id(budgetId).build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(budget));

        UpdateBudgetRequest req = UpdateBudgetRequest.builder()
                .budgetId(budgetId)
                .categoryId(null)
                .amount(new BigDecimal("300.00"))
                .build();

        when(budgetRepository.save(budget)).thenReturn(budget);
        BudgetResponse expected = BudgetResponse.builder().id(budgetId).amount(new BigDecimal("300.00")).build();
        when(budgetMapper.toResponse(budget)).thenReturn(expected);

        BudgetResponse result = command.execute(req);

        verify(categoryRepository, never()).findByIdAndUserId(anyLong(), anyLong());
        verify(budgetMapper).updateEntity(budget, req, null);
        verify(budgetRepository).save(budget);
        assertEquals(expected, result);
    }
}

