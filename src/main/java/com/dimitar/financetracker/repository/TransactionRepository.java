package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    List<Transaction> findByUserId(Long userId);

    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId,
                                                            LocalDate transactionDateAfter,
                                                            LocalDate transactionDateBefore);

    List<Transaction> findByUserIdAndAmountGreaterThan(Long userId, BigDecimal amount);

    List<Transaction> findByUserIdAndAmountLessThan(Long userId, BigDecimal amount);

    List<Transaction> findByUserIdAndAmountBetween(Long userId, BigDecimal minAmount, BigDecimal maxAmount);

    List<Transaction> findByUserIdAndAmountGreaterThanEqual(Long userId, BigDecimal minAmount);

    List<Transaction> findByUserIdAndAmountLessThanEqual(Long userId, BigDecimal maxAmount);

    List<Transaction> findByUserIdAndDescriptionContainingIgnoreCase(Long userId, String searchTerm);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
        "WHERE t.user.id = :userId " +
        "AND t.category.type = :type")
    BigDecimal sumAmountByUserAndType(
        @Param("userId") Long userId,
        @Param("type") CategoryType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
        "WHERE t.user.id = :userId " +
        "AND t.category.type = :type " +
        "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserAndTypeAndDateRange(
        @Param("userId") Long userId,
        @Param("type") CategoryType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
        "WHERE t.user.id = :userId " +
        "AND t.category.id = :categoryId " +
        "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByUserAndCategoryAndDateRange(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(t) FROM Transaction t " +
        "WHERE t.user.id = :userId " +
        "AND t.category.type = :type")
    Long countByUserAndType(
        @Param("userId") Long userId,
        @Param("type") CategoryType type);

    @Query("SELECT COUNT(t) FROM Transaction t " +
        "WHERE t.user.id = :userId")
    Long countByUser(@Param("userId") Long userId);

    @Query("SELECT AVG(t.amount) FROM Transaction t " +
        "WHERE t.user.id = :userId " +
        "AND t.category.type = :type")
    BigDecimal avgAmountByUserAndType(
        @Param("userId") Long userId,
        @Param("type") CategoryType type);

}
