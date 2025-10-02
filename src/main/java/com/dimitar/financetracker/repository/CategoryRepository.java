package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    List<Category> findByUserIdAndType(Long userId, CategoryType type);

    List<Category> findAllByUserId(Long userId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    Optional<Category> findByUserIdAndName(Long userId, String name);

    Boolean existsByUserIdAndName(Long userId, String name);

    List<Category> findAllByUserIdAndType(Long userId, CategoryType type);

    List<Category> findAllByUserIdAndNameContaining(Long userId, String name);

}
