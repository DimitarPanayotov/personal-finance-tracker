package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    // Custom query with join (if needed later)
    //@Query("SELECT u FROM User u LEFT JOIN FETCH u.categories WHERE u.id = :userId")
    //Optional<User> findByIdWithCategories(@Param("userId") Long userId);

    // Count methods for analytics
    //Long countByUsernameContaining(String searchTerm);
}
