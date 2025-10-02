package com.dimitar.financetracker.service.query.budget;

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
class GetBudgetByIdQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private BudgetMapper budgetMapper;

    private GetBudgetByIdQuery query;

    @BeforeEach
    void setUp() { query = new GetBudgetByIdQuery(authenticationFacade, budgetRepository, budgetMapper); }

    @Test
    void execute_returnsMapped_whenFound() {
        Long userId = 2L; Long budgetId = 10L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Budget b = Budget.builder().id(budgetId).build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(b));
        BudgetResponse expected = BudgetResponse.builder().id(budgetId).build();
        when(budgetMapper.toResponse(b)).thenReturn(expected);

        BudgetResponse result = query.execute(budgetId);
        assertEquals(expected, result);
        verify(budgetRepository).findByIdAndUserId(budgetId, userId);
        verify(budgetMapper).toResponse(b);
        verifyNoMoreInteractions(budgetRepository, budgetMapper);
    }

    @Test
    void execute_throwsWhenNotFound() {
        Long userId = 2L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(budgetRepository.findByIdAndUserId(404L, userId)).thenReturn(Optional.empty());
        assertThrows(BudgetDoesNotExistException.class, () -> query.execute(404L));
        verify(budgetRepository).findByIdAndUserId(404L, userId);
        verifyNoInteractions(budgetMapper);
    }
}
