package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.Budget;
import com.dimitar.financetracker.model.BudgetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndIsActiveTrue(Long userId);

    List<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Budget> findByUserIdAndPeriod(Long userId, BudgetPeriod period);

}
