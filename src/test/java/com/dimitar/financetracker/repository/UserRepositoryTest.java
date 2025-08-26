package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void save_shouldPersistUser() {
        User savedUser = userRepository.save(testUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("password123");
    }

    @Test
    void findByUsername_shouldReturnUserWhenExists() {
        entityManager.persistAndFlush(testUser);

        Optional<User> found = userRepository.findByUsername("testuser");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByUsername_shouldReturnEmptyWhenNotExists() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertThat(found).isNotPresent();
    }

    @Test
    void findByUsername_shouldBeCaseSensitive() {
        entityManager.persistAndFlush(testUser);

        Optional<User> found = userRepository.findByUsername("TESTUSER");

        assertThat(found).isNotPresent();
    }

    @Test
    void findByEmail_shouldReturnUserWhenExists() {
        entityManager.persistAndFlush(testUser);

        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findByEmail_shouldReturnEmptyWhenNotExists() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isNotPresent();
    }

    @Test
    void findByEmail_shouldBeCaseSensitive() {
        entityManager.persistAndFlush(testUser);

        Optional<User> found = userRepository.findByEmail("TEST@EXAMPLE.COM");

        assertThat(found).isNotPresent();
    }

    @Test
    void existsByUsername_shouldReturnTrueWhenExists() {
        entityManager.persistAndFlush(testUser);

        Boolean exists = userRepository.existsByUsername("testuser");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalseWhenNotExists() {
        Boolean exists = userRepository.existsByUsername("nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByUsername_shouldBeCaseSensitive() {
        entityManager.persistAndFlush(testUser);

        Boolean exists = userRepository.existsByUsername("TESTUSER");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_shouldReturnTrueWhenExists() {
        entityManager.persistAndFlush(testUser);

        Boolean exists = userRepository.existsByEmail("test@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalseWhenNotExists() {
        Boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_shouldBeCaseSensitive() {
        entityManager.persistAndFlush(testUser);

        Boolean exists = userRepository.existsByEmail("TEST@EXAMPLE.COM");

        assertThat(exists).isFalse();
    }

    @Test
    void findById_shouldReturnUserWhenExists() {
        User savedUser = entityManager.persistAndFlush(testUser);

        Optional<User> found = userRepository.findById(savedUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(savedUser.getId());
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<User> found = userRepository.findById(999L);

        assertThat(found).isNotPresent();
    }

    @Test
    void deleteById_shouldRemoveUser() {
        User savedUser = entityManager.persistAndFlush(testUser);
        Long userId = savedUser.getId();

        userRepository.deleteById(userId);
        entityManager.flush();

        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isNotPresent();
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .password("password1")
                .build();

        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password("password2")
                .build();

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        var users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername)
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void save_shouldUpdateExistingUser() {
        User savedUser = entityManager.persistAndFlush(testUser);

        savedUser.setUsername("updateduser");
        savedUser.setEmail("updated@example.com");

        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(updatedUser.getUsername()).isEqualTo("updateduser");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void uniqueConstraints_shouldBeEnforced() {
        entityManager.persistAndFlush(testUser);

        User duplicateUsernameUser = User.builder()
                .username("testuser") // duplicate username
                .email("different@example.com")
                .password("password123")
                .build();

        User duplicateEmailUser = User.builder()
                .username("differentuser")
                .email("test@example.com") // duplicate email
                .password("password123")
                .build();

        // These should throw exceptions due to unique constraints
        // Note: The exact exception depends on your database configuration
        // In a real test environment, you might want to catch specific exceptions
        // Test duplicate username constraint
        assertThatThrownBy(() -> {
            userRepository.save(duplicateUsernameUser);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);

        // Clear the entity manager to reset session state after exception
        entityManager.clear();
        // Test duplicate email constraint
        assertThatThrownBy(() -> {
            userRepository.save(duplicateEmailUser);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);

        entityManager.clear();

        assertThat(userRepository.existsByUsername("testuser")).isTrue();
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
    }
}

