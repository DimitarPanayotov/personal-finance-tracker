package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.model.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndIsActiveTrue(Long userId);

    List<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Budget> findByUserIdAndPeriod(Long userId, BudgetPeriod period);


    // Active budgets for date
//    @Query("SELECT b FROM Budget b " +
//        "WHERE b.user.id = :userId " +
//        "AND b.isActive = true " +
//        "AND :date BETWEEN b.startDate AND b.endDate")
//    List<Budget> findActiveBudgetsByUserAndDate(
//        @Param("userId") Long userId,
//        @Param("date") LocalDate date);

    // Check for overlapping budgets
//    @Query("SELECT COUNT(b) > 0 FROM Budget b " +
//        "WHERE b.user.id = :userId " +
//        "AND b.category.id = :categoryId " +
//        "AND b.isActive = true " +
//        "AND (b.startDate BETWEEN :startDate AND :endDate " +
//        "OR b.endDate BETWEEN :startDate AND :endDate " +
//        "OR :startDate BETWEEN b.startDate AND b.endDate)")
//    Boolean existsOverlappingBudget(
//        @Param("userId") Long userId,
//        @Param("categoryId") Long categoryId,
//        @Param("startDate") LocalDate startDate,
//        @Param("endDate") LocalDate endDate);

    // Find budgets ending soon (for notifications)
//    @Query("SELECT b FROM Budget b " +
//        "WHERE b.user.id = :userId " +
//        "AND b.isActive = true " +
//        "AND b.endDate BETWEEN :startDate AND :endDate")
//    List<Budget> findBudgetsEndingSoon(
//        @Param("userId") Long userId,
//        @Param("startDate") LocalDate startDate,
//        @Param("endDate") LocalDate endDate);

    // Budget utilization query
//    @Query("SELECT b, COALESCE(SUM(t.amount), 0) as spent " +
//        "FROM Budget b LEFT JOIN Transaction t " +
//        "ON t.user.id = b.user.id AND t.category.id = b.category.id " +
//        "AND t.transactionDate BETWEEN b.startDate AND b.endDate " +
//        "WHERE b.user.id = :userId AND b.id = :budgetId " +
//        "GROUP BY b.id")
//    Optional<Object[]> findBudgetWithSpentAmount(
//        @Param("userId") Long userId,
//        @Param("budgetId") Long budgetId);
}
