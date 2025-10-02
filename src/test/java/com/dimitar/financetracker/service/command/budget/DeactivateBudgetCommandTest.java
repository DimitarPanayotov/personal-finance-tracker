package com.dimitar.financetracker.service.command.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeactivateBudgetCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private BudgetMapper budgetMapper;

    private DeactivateBudgetCommand command;

    @BeforeEach
    void setUp() { command = new DeactivateBudgetCommand(authenticationFacade, budgetRepository, budgetMapper); }

    @Test
    void execute_deactivates_whenActiveOrNull() {
        Long userId = 7L; Long budgetId = 22L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        // case 1: null isActive
        Budget b1 = Budget.builder().id(budgetId).isActive(null).build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(b1));
        when(budgetRepository.save(b1)).thenReturn(b1);
        when(budgetMapper.toResponse(b1)).thenReturn(BudgetResponse.builder().id(budgetId).isActive(false).build());

        BudgetResponse r1 = command.execute(budgetId);
        assertFalse(r1.getIsActive());
        assertEquals(false, b1.getIsActive());
        verify(budgetRepository).save(b1);

        // case 2: active true
        reset(budgetRepository, budgetMapper);
        Budget b2 = Budget.builder().id(budgetId).isActive(true).build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(b2));
        when(budgetRepository.save(b2)).thenReturn(b2);
        when(budgetMapper.toResponse(b2)).thenReturn(BudgetResponse.builder().id(budgetId).isActive(false).build());

        BudgetResponse r2 = command.execute(budgetId);
        assertFalse(r2.getIsActive());
        assertEquals(false, b2.getIsActive());
        verify(budgetRepository).save(b2);
    }

    @Test
    void execute_noSave_whenAlreadyInactive() {
        Long userId = 7L; Long budgetId = 22L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Budget b = Budget.builder().id(budgetId).isActive(false).build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(b));
        when(budgetMapper.toResponse(b)).thenReturn(BudgetResponse.builder().id(budgetId).isActive(false).build());

        BudgetResponse resp = command.execute(budgetId);
        assertFalse(resp.getIsActive());
        verify(budgetRepository, never()).save(any());
    }

    @Test
    void execute_throwsWhenNotFound() {
        Long userId = 7L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(budgetRepository.findByIdAndUserId(404L, userId)).thenReturn(Optional.empty());
        assertThrows(BudgetDoesNotExistException.class, () -> command.execute(404L));
    }
}

