package com.dimitar.financetracker.service.command.budget;

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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteBudgetCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private BudgetRepository budgetRepository;

    private DeleteBudgetCommand command;

    @BeforeEach
    void setUp() { command = new DeleteBudgetCommand(authenticationFacade, budgetRepository); }

    @Test
    void execute_deletesBudget_whenFoundForUser() {
        Long userId = 10L; Long budgetId = 77L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Budget budget = Budget.builder().id(budgetId).build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(budget));

        command.execute(budgetId);

        verify(budgetRepository).delete(budget);
        verifyNoMoreInteractions(budgetRepository);
    }

    @Test
    void execute_throwsWhenBudgetNotFound() {
        Long userId = 10L; Long budgetId = 999L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.empty());

        assertThrows(BudgetDoesNotExistException.class, () -> command.execute(budgetId));
        verify(budgetRepository, never()).delete(any());
    }
}

