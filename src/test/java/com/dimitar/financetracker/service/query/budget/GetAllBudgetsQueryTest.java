package com.dimitar.financetracker.service.query.budget;

import com.dimitar.financetracker.dto.mapper.BudgetMapper;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllBudgetsQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private BudgetMapper budgetMapper;

    private GetAllBudgetsQuery query;

    @BeforeEach
    void setUp() { query = new GetAllBudgetsQuery(authenticationFacade, budgetRepository, budgetMapper); }

    @Test
    void execute_returnsMappedBudgetsForUser() {
        Long userId = 1L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Budget b1 = Budget.builder().id(1L).build();
        Budget b2 = Budget.builder().id(2L).build();
        when(budgetRepository.findByUserId(userId)).thenReturn(List.of(b1, b2));

        BudgetResponse r1 = BudgetResponse.builder().id(1L).build();
        BudgetResponse r2 = BudgetResponse.builder().id(2L).build();
        when(budgetMapper.toResponse(b1)).thenReturn(r1);
        when(budgetMapper.toResponse(b2)).thenReturn(r2);

        List<BudgetResponse> result = query.execute(null);
        assertEquals(List.of(r1, r2), result);
        verify(budgetRepository).findByUserId(userId);
        verify(budgetMapper).toResponse(b1);
        verify(budgetMapper).toResponse(b2);
        verifyNoMoreInteractions(budgetRepository, budgetMapper);
    }
}
