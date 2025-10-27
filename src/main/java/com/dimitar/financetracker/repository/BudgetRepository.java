package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.model.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndIsActiveTrue(Long userId);

    List<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Budget> findByUserIdAndPeriod(Long userId, BudgetPeriod period);


    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.category.id = :categoryId " +
           "AND b.isActive = true " +
           "AND b.id != :excludeBudgetId " +
           "AND b.startDate < :endDate " +
           "AND b.endDate > :startDate")
    List<Budget> findOverlappingActiveBudgets(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("excludeBudgetId") Long excludeBudgetId
    );

    default List<Budget> findOverlappingActiveBudgets(
        Long userId,
        Long categoryId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        return findOverlappingActiveBudgets(userId, categoryId, startDate, endDate, -1L);
    }
}
