package com.dimitar.financetracker.service.query.budget;

import com.dimitar.financetracker.dto.response.budget.BudgetUsageResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.budget.BudgetDoesNotExistException;
import com.dimitar.financetracker.model.BudgetPeriod;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.repository.TransactionRepository;
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
class GetBudgetUsageQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private GetBudgetUsageQuery query;

    @BeforeEach
    void setUp() { query = new GetBudgetUsageQuery(authenticationFacade, budgetRepository, transactionRepository); }

    @Test
    void execute_buildsUsage_forSingleBudget() {
        Long userId = 9L; Long budgetId = 1L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        User user = User.builder().id(userId).build();
        Category cat = Category.builder().id(10L).name("Food").build();
        Budget b = Budget.builder()
                .id(budgetId).user(user).category(cat)
                .amount(new BigDecimal("200.00"))
                .startDate(LocalDate.of(2025,1,1))
                .endDate(LocalDate.of(2025,1,31))
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();
        when(budgetRepository.findByIdAndUserId(budgetId, userId)).thenReturn(Optional.of(b));

        when(transactionRepository.sumAmountByUserAndCategoryAndDateRange(userId, 10L, b.getStartDate(), b.getEndDate()))
                .thenReturn(new BigDecimal("50.25"));

        BudgetUsageResponse resp = query.execute(budgetId);
        assertEquals(budgetId, resp.getId());
        assertEquals(new BigDecimal("200.00"), resp.getAmount());
        assertEquals(new BigDecimal("50.25"), resp.getSpent());
        assertEquals(new BigDecimal("149.75"), resp.getRemaining());
        assertEquals(new BigDecimal("25.13"), resp.getPercentUsed());
    }

    @Test
    void execute_throwsWhenBudgetNotFound() {
        Long userId = 9L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(budgetRepository.findByIdAndUserId(404L, userId)).thenReturn(Optional.empty());
        assertThrows(BudgetDoesNotExistException.class, () -> query.execute(404L));
    }
}
