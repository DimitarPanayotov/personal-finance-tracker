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

    //List<Transaction> findByUserIdAndTransactionDateYearAndTransactionDateMonth(Long userId,
    //                                                                          int transactionDateYear,
    //                                                                        short transactionDateMonth);

    List<Transaction> findByUserIdAndAmountGreaterThan(Long userId, BigDecimal amount);

    List<Transaction> findByUserIdAndAmountLessThan(Long userId, BigDecimal amount);

    List<Transaction> findByUserIdAndDescriptionContainingIgnoreCase(Long userId, String searchTerm);

    // Analytics queries
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

    // Monthly summary
//    @Query("SELECT FUNCTION('YEAR', t.transactionDate) as year, " +
//        "FUNCTION('MONTH', t.transactionDate) as month, " +
//        "t.category.type as type, " +
//        "SUM(t.amount) as total " +
//        "FROM Transaction t " +
//        "WHERE t.user.id = :userId " +
//        "GROUP BY year, month, type " +
//        "ORDER BY year, month")
//    List<Object[]> getMonthlySummary(@Param("userId") Long userId);

    // Recent transactions with category fetch
//    @Query("SELECT t FROM Transaction t " +
//        "JOIN FETCH t.category " +
//        "WHERE t.user.id = :userId " +
//        "ORDER BY t.transactionDate DESC, t.createdAt DESC")
//    List<Transaction> findRecentTransactionsWithCategory(@Param("userId") Long userId, Pageable pageable);
}
