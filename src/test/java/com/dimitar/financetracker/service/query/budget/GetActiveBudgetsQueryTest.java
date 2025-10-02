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
class GetActiveBudgetsQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private BudgetMapper budgetMapper;

    private GetActiveBudgetsQuery query;

    @BeforeEach
    void setUp() { query = new GetActiveBudgetsQuery(authenticationFacade, budgetRepository, budgetMapper); }

    @Test
    void execute_returnsMappedActiveBudgets() {
        Long userId = 4L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Budget b = Budget.builder().id(1L).build();
        when(budgetRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(List.of(b));
        BudgetResponse r = BudgetResponse.builder().id(1L).build();
        when(budgetMapper.toResponse(b)).thenReturn(r);

        List<BudgetResponse> result = query.execute(null);
        assertEquals(List.of(r), result);
        verify(budgetRepository).findByUserIdAndIsActiveTrue(userId);
        verify(budgetMapper).toResponse(b);
        verifyNoMoreInteractions(budgetRepository, budgetMapper);
    }
}
