package com.dimitar.financetracker.service.query.user;

import com.dimitar.financetracker.dto.response.user.UserStatisticsResponse;
import com.dimitar.financetracker.model.CategoryType;
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
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserStatisticsQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private TransactionRepository transactionRepository;

    private GetUserStatisticsQuery query;

    @BeforeEach
    void setUp() {
        query = new GetUserStatisticsQuery(authenticationFacade, transactionRepository);
    }

    @Test
    void execute_returnsComputedStatistics_withValues() {
        // Arrange
        Long userId = 42L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        when(transactionRepository.sumAmountByUserAndType(userId, CategoryType.INCOME))
                .thenReturn(new BigDecimal("1000"));
        when(transactionRepository.sumAmountByUserAndType(userId, CategoryType.EXPENSE))
                .thenReturn(new BigDecimal("400"));

        when(transactionRepository.countByUser(userId)).thenReturn(10L);
        when(transactionRepository.countByUserAndType(userId, CategoryType.INCOME)).thenReturn(4L);
        when(transactionRepository.countByUserAndType(userId, CategoryType.EXPENSE)).thenReturn(6L);

        when(transactionRepository.avgAmountByUserAndType(userId, CategoryType.INCOME))
                .thenReturn(new BigDecimal("250.123")); // rounds to 250.12
        when(transactionRepository.avgAmountByUserAndType(userId, CategoryType.EXPENSE))
                .thenReturn(new BigDecimal("66.665")); // rounds to 66.67

        // Monthly stats for current month range
        YearMonth now = YearMonth.now();
        LocalDate start = now.atDay(1);
        LocalDate end = now.atEndOfMonth();

        when(transactionRepository.sumAmountByUserAndTypeAndDateRange(eq(userId), eq(CategoryType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("300"));
        when(transactionRepository.sumAmountByUserAndTypeAndDateRange(eq(userId), eq(CategoryType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("120"));

        ArgumentCaptor<LocalDate> startCaptor = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endCaptor = ArgumentCaptor.forClass(LocalDate.class);

        // Act
        UserStatisticsResponse resp = query.execute(null);

        // Assert amounts
        assertEquals(new BigDecimal("1000"), resp.getTotalIncome());
        assertEquals(new BigDecimal("400"), resp.getTotalExpenses());
        assertEquals(new BigDecimal("600"), resp.getNetBalance());

        // Assert counts
        assertEquals(10L, resp.getTotalTransactions());
        assertEquals(4L, resp.getTotalIncomeTransactions());
        assertEquals(6L, resp.getTotalExpenseTransactions());

        // Assert averages (rounded)
        assertEquals(new BigDecimal("250.12"), resp.getAverageIncomePerTransaction());
        assertEquals(new BigDecimal("66.67"), resp.getAverageExpensePerTransaction());

        // Verify date range used for monthly computations and assert monthly results
        verify(transactionRepository).sumAmountByUserAndTypeAndDateRange(eq(userId), eq(CategoryType.INCOME), startCaptor.capture(), endCaptor.capture());
        LocalDate capturedStart = startCaptor.getValue();
        LocalDate capturedEnd = endCaptor.getValue();
        assertEquals(start, capturedStart);
        assertEquals(end, capturedEnd);

        assertEquals(new BigDecimal("300"), resp.getMonthlyIncome());
        assertEquals(new BigDecimal("120"), resp.getMonthlyExpenses());
        assertEquals(new BigDecimal("180"), resp.getMonthlyNetBalance());
    }

    @Test
    void execute_handlesNullsGracefully() {
        Long userId = 101L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        // Return nulls for monetary aggregates
        when(transactionRepository.sumAmountByUserAndType(eq(userId), any(CategoryType.class)))
                .thenReturn(null);
        when(transactionRepository.avgAmountByUserAndType(eq(userId), any(CategoryType.class)))
                .thenReturn(null);
        when(transactionRepository.sumAmountByUserAndTypeAndDateRange(eq(userId), any(CategoryType.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);

        // Reasonable defaults for counts
        when(transactionRepository.countByUser(userId)).thenReturn(0L);
        when(transactionRepository.countByUserAndType(eq(userId), any(CategoryType.class))).thenReturn(0L);

        UserStatisticsResponse resp = query.execute(null);

        // All monetary fields should be zero
        assertEquals(BigDecimal.ZERO, resp.getTotalIncome());
        assertEquals(BigDecimal.ZERO, resp.getTotalExpenses());
        assertEquals(BigDecimal.ZERO, resp.getNetBalance());
        assertEquals(BigDecimal.ZERO, resp.getAverageIncomePerTransaction());
        assertEquals(BigDecimal.ZERO, resp.getAverageExpensePerTransaction());
        assertEquals(BigDecimal.ZERO, resp.getMonthlyIncome());
        assertEquals(BigDecimal.ZERO, resp.getMonthlyExpenses());
        assertEquals(BigDecimal.ZERO, resp.getMonthlyNetBalance());

        // Counts should reflect stubbed values
        assertEquals(0L, resp.getTotalTransactions());
        assertEquals(0L, resp.getTotalIncomeTransactions());
        assertEquals(0L, resp.getTotalExpenseTransactions());
    }
}

