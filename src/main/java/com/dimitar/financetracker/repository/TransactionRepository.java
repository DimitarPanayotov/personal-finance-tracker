package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.model.CategoryType;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {"category", "user"})
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = {"category", "user"})
    List<Transaction> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"category", "user"})
    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "user"})
    List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId);

    @EntityGraph(attributePaths = {"category", "user"})
    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId,
                                                            LocalDate transactionDateAfter,
                                                            LocalDate transactionDateBefore);

    @EntityGraph(attributePaths = {"category", "user"})
    List<Transaction> findByUserIdAndAmountGreaterThan(Long userId, BigDecimal amount);

    @EntityGraph(attributePaths = {"category", "user"})
    List<Transaction> findByUserIdAndAmountLessThan(Long userId, BigDecimal amount);

    @EntityGraph(attributePaths = {"category", "user"})
    List<Transaction> findByUserIdAndAmountBetween(Long userId, BigDecimal minAmount, BigDecimal maxAmount);

    @EntityGraph(attributePaths = {"category", "user"})
    List<Transaction> findByUserIdAndAmountGreaterThanEqual(Long userId, BigDecimal minAmount);

    @EntityGraph(attributePaths = {"category", "user"})
    List<Transaction> findByUserIdAndAmountLessThanEqual(Long userId, BigDecimal maxAmount);

    @EntityGraph(attributePaths = {"category", "user"})
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
