package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    List<Category> findByUserIdAndType(Long userId, CategoryType type);

    Optional<Category> findByUserIdAndName(Long userId, String name);

    Boolean existsByUserIdAndName(Long userId, String name);

    // Find categories with transaction count (for analytics)
//    @Query("SELECT c, COUNT(t) as transactionCount " +
//        "FROM Category c LEFT JOIN c.transactions t " +
//        "WHERE c.user.id = :userId " +
//        "GROUP BY c.id")
//    List<Object[]> findCategoriesWithTransactionCount(@Param("userId") Long userId);

    // Find most used categories
//    @Query("SELECT c FROM Category c " +
//        "WHERE c.user.id = :userId " +
//        "AND c.id IN (" +
//        "   SELECT t.category.id FROM Transaction t " +
//        "   WHERE t.user.id = :userId " +
//        "   GROUP BY t.category.id " +
//        "   ORDER BY COUNT(t) DESC" +
//        ")")
//    List<Category> findMostUsedCategories(@Param("userId") Long userId, Pageable pageable);
}
