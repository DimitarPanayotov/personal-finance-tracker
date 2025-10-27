package com.dimitar.financetracker.service.command.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.exception.budget.OverlappingBudgetException;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateBudgetCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private BudgetMapper budgetMapper;

    private ActivateBudgetCommand command;

    @BeforeEach
    void setUp() { command = new ActivateBudgetCommand(authenticationFacade, budgetRepository, budgetMapper); }

    @Test
    void execute_activates_whenInactiveOrNull() {
        Long userId = 5L; Long budgetId = 11L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        // case 1: null isActive - budget has no category so validation is skipped
        Budget b1 = Budget.builder()
                .id(budgetId)
                .isActive(null)
                .category(null)
                .build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(b1));
        when(budgetRepository.save(b1)).thenReturn(b1);
        when(budgetMapper.toResponse(b1)).thenReturn(BudgetResponse.builder().id(budgetId).isActive(true).build());

        BudgetResponse r1 = command.execute(budgetId);
        assertTrue(r1.getIsActive());
        assertEquals(true, b1.getIsActive());
        verify(budgetRepository).save(b1);

        // case 2: inactive false - budget has no category so validation is skipped
        reset(budgetRepository, budgetMapper);
        Budget b2 = Budget.builder()
                .id(budgetId)
                .isActive(false)
                .category(null)
                .build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(b2));
        when(budgetRepository.save(b2)).thenReturn(b2);
        when(budgetMapper.toResponse(b2)).thenReturn(BudgetResponse.builder().id(budgetId).isActive(true).build());

        BudgetResponse r2 = command.execute(budgetId);
        assertTrue(r2.getIsActive());
        assertEquals(true, b2.getIsActive());
        verify(budgetRepository).save(b2);
    }

    @Test
    void execute_noSave_whenAlreadyActive() {
        Long userId = 5L; Long budgetId = 11L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Budget b = Budget.builder().id(budgetId).isActive(true).build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(b));
        when(budgetMapper.toResponse(b)).thenReturn(BudgetResponse.builder().id(budgetId).isActive(true).build());

        BudgetResponse resp = command.execute(budgetId);
        assertTrue(resp.getIsActive());
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void execute_throwsWhenNotFound() {
        Long userId = 5L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(budgetRepository.findByIdAndUserId(404L, userId)).thenReturn(Optional.empty());
        assertThrows(BudgetDoesNotExistException.class, () -> command.execute(404L));
    }

    @Test
    void execute_throwsOverlappingBudgetException_whenActivatingWouldCreateConflict() {
        Long userId = 5L;
        Long budgetId = 11L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Category category = Category.builder().id(10L).name("Groceries").build();
        Budget budgetToActivate = Budget.builder()
                .id(budgetId)
                .category(category)
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 3, 31))
                .isActive(false)
                .build();

        when(budgetRepository.findByIdAndUserId(budgetId, userId))
                .thenReturn(Optional.of(budgetToActivate));

        // Existing active budget that overlaps
        Budget existingActiveBudget = Budget.builder()
                .id(99L)
                .category(category)
                .amount(new BigDecimal("500.00"))
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 3, 31))
                .isActive(true)
                .build();

        when(budgetRepository.findOverlappingActiveBudgets(
                userId, 10L,
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 31),
                budgetId))
                .thenReturn(List.of(existingActiveBudget));

        OverlappingBudgetException exception = assertThrows(
                OverlappingBudgetException.class,
                () -> command.execute(budgetId)
        );

        assertTrue(exception.getMessage().contains("Groceries"));
        assertTrue(exception.getMessage().contains("2025-03-01"));
        assertTrue(exception.getMessage().contains("2025-03-31"));
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void execute_success_whenActivatingWithNoConflicts() {
        Long userId = 5L;
        Long budgetId = 11L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Category category = Category.builder().id(10L).name("Groceries").build();
        Budget budgetToActivate = Budget.builder()
                .id(budgetId)
                .category(category)
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2025, 4, 30))
                .isActive(false)
                .build();

        when(budgetRepository.findByIdAndUserId(budgetId, userId))
                .thenReturn(Optional.of(budgetToActivate));

        when(budgetRepository.findOverlappingActiveBudgets(
                userId, 10L,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                budgetId))
                .thenReturn(Collections.emptyList());

        when(budgetRepository.save(budgetToActivate)).thenReturn(budgetToActivate);
        BudgetResponse response = BudgetResponse.builder()
                .id(budgetId)
                .isActive(true)
                .build();
        when(budgetMapper.toResponse(budgetToActivate)).thenReturn(response);

        BudgetResponse result = command.execute(budgetId);

        assertTrue(result.getIsActive());
        assertEquals(true, budgetToActivate.getIsActive());
        verify(budgetRepository).findOverlappingActiveBudgets(
                userId, 10L,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                budgetId);
        verify(budgetRepository).save(budgetToActivate);
    }
}
