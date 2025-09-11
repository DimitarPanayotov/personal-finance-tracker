package com.dimitar.financetracker.service.query.user;

import com.dimitar.financetracker.dto.response.user.UserStatisticsResponse;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

@Component
@RequiredArgsConstructor
public class GetUserStatisticsQuery implements Query<Void, UserStatisticsResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final TransactionRepository transactionRepository;

    @Override
    public UserStatisticsResponse execute(Void input) {
        Long userId = authenticationFacade.getAuthenticatedUserId();

        // Calculate overall statistics
        BigDecimal totalIncome = getSafeAmount(transactionRepository.sumAmountByUserAndType(userId, CategoryType.INCOME));
        BigDecimal totalExpenses = getSafeAmount(transactionRepository.sumAmountByUserAndType(userId, CategoryType.EXPENSE));
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        // Calculate transaction counts
        Long totalTransactions = transactionRepository.countByUser(userId);
        Long totalIncomeTransactions = transactionRepository.countByUserAndType(userId, CategoryType.INCOME);
        Long totalExpenseTransactions = transactionRepository.countByUserAndType(userId, CategoryType.EXPENSE);

        // Calculate averages
        BigDecimal averageIncomePerTransaction = getSafeAverage(transactionRepository.avgAmountByUserAndType(userId, CategoryType.INCOME));
        BigDecimal averageExpensePerTransaction = getSafeAverage(transactionRepository.avgAmountByUserAndType(userId, CategoryType.EXPENSE));

        // Calculate monthly statistics (current month)
        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();

        BigDecimal monthlyIncome = getSafeAmount(transactionRepository.sumAmountByUserAndTypeAndDateRange(
            userId, CategoryType.INCOME, monthStart, monthEnd));
        BigDecimal monthlyExpenses = getSafeAmount(transactionRepository.sumAmountByUserAndTypeAndDateRange(
            userId, CategoryType.EXPENSE, monthStart, monthEnd));
        BigDecimal monthlyNetBalance = monthlyIncome.subtract(monthlyExpenses);

        return UserStatisticsResponse.builder()
            .totalIncome(totalIncome)
            .totalExpenses(totalExpenses)
            .netBalance(netBalance)
            .totalTransactions(totalTransactions)
            .totalIncomeTransactions(totalIncomeTransactions)
            .totalExpenseTransactions(totalExpenseTransactions)
            .averageIncomePerTransaction(averageIncomePerTransaction)
            .averageExpensePerTransaction(averageExpensePerTransaction)
            .monthlyIncome(monthlyIncome)
            .monthlyExpenses(monthlyExpenses)
            .monthlyNetBalance(monthlyNetBalance)
            .build();
    }

    private BigDecimal getSafeAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    private BigDecimal getSafeAverage(BigDecimal average) {
        return average != null ? average.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
}
