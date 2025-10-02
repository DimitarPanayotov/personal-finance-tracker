package com.dimitar.financetracker.service.query.budget;

import com.dimitar.financetracker.dto.response.budget.BudgetUsageResponse;
import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.model.BudgetPeriod;
import com.dimitar.financetracker.repository.BudgetRepository;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllBudgetsUsageQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private GetAllBudgetsUsageQuery query;

    @BeforeEach
    void setUp() { query = new GetAllBudgetsUsageQuery(authenticationFacade, budgetRepository, transactionRepository); }

    @Test
    void execute_buildsUsageForEachBudget_andHandlesNullSpent() {
        Long userId = 9L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        User user = User.builder().id(userId).build();
        Category cat1 = Category.builder().id(10L).name("Food").build();
        Category cat2 = Category.builder().id(11L).name("Transport").build();

        Budget b1 = Budget.builder()
                .id(1L).user(user).category(cat1)
                .amount(new BigDecimal("100.00"))
                .startDate(LocalDate.of(2025,1,1))
                .endDate(LocalDate.of(2025,1,31))
                .period(BudgetPeriod.MONTHLY)
                .isActive(true)
                .build();
        Budget b2 = Budget.builder()
                .id(2L).user(user).category(cat2)
                .amount(new BigDecimal("50.00"))
                .startDate(LocalDate.of(2025,1,1))
                .endDate(LocalDate.of(2025,1,31))
                .period(BudgetPeriod.MONTHLY)
                .isActive(false)
                .build();

        when(budgetRepository.findByUserId(userId)).thenReturn(List.of(b1, b2));

        // Spent: first budget returns null -> treated as 0; second returns 30.55
        when(transactionRepository.sumAmountByUserAndCategoryAndDateRange(userId, 10L, b1.getStartDate(), b1.getEndDate()))
                .thenReturn(null);
        when(transactionRepository.sumAmountByUserAndCategoryAndDateRange(userId, 11L, b2.getStartDate(), b2.getEndDate()))
                .thenReturn(new BigDecimal("30.55"));

        List<BudgetUsageResponse> result = query.execute(null);

        assertEquals(2, result.size());

        BudgetUsageResponse u1 = result.get(0);
        assertEquals(1L, u1.getId());
        assertEquals(new BigDecimal("100.00"), u1.getAmount());
        assertEquals(new BigDecimal("0"), u1.getSpent());
        assertEquals(new BigDecimal("100.00"), u1.getRemaining());
        assertEquals(new BigDecimal("0.00"), u1.getPercentUsed());

        BudgetUsageResponse u2 = result.get(1);
        assertEquals(2L, u2.getId());
        assertEquals(new BigDecimal("50.00"), u2.getAmount());
        assertEquals(new BigDecimal("30.55"), u2.getSpent());
        assertEquals(new BigDecimal("19.45"), u2.getRemaining());
        assertEquals(new BigDecimal("61.10"), u2.getPercentUsed());
    }
}

