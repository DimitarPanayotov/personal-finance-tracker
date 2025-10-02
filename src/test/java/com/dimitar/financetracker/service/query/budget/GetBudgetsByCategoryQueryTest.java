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
class GetBudgetsByCategoryQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private BudgetMapper budgetMapper;

    private GetBudgetsByCategoryQuery query;

    @BeforeEach
    void setUp() { query = new GetBudgetsByCategoryQuery(authenticationFacade, budgetRepository, budgetMapper); }

    @Test
    void execute_returnsMappedForCategory() {
        Long userId = 3L; Long categoryId = 7L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Budget b = Budget.builder().id(1L).build();
        when(budgetRepository.findByUserIdAndCategoryId(userId, categoryId)).thenReturn(List.of(b));
        BudgetResponse r = BudgetResponse.builder().id(1L).build();
        when(budgetMapper.toResponse(b)).thenReturn(r);
        List<BudgetResponse> result = query.execute(categoryId);
        assertEquals(List.of(r), result);
        // Verify interactions like other query tests for consistency
        verify(budgetRepository).findByUserIdAndCategoryId(userId, categoryId);
        verify(budgetMapper).toResponse(b);
        verifyNoMoreInteractions(budgetRepository, budgetMapper);
    }
}
