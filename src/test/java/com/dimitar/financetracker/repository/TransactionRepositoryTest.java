package com.dimitar.financetracker.repository;

import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.model.CategoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private User testUser;
    private User anotherUser;
    private Category expenseCategory;
    private Category incomeCategory;

    @BeforeEach
    void setUp() {
        testUser = createAndPersistUser("testuser", "test@example.com");
        anotherUser = createAndPersistUser("anotheruser", "another@example.com");

        expenseCategory = createAndPersistCategory(testUser, "Food", CategoryType.EXPENSE);
        incomeCategory = createAndPersistCategory(testUser, "Salary", CategoryType.INCOME);
    }

    @Test
    void save_shouldPersistTransaction() {
        Transaction transaction = Transaction.builder()
                .user(testUser)
                .category(expenseCategory)
                .amount(new BigDecimal("25.50"))
                .description("Lunch")
                .transactionDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        assertThat(savedTransaction.getId()).isNotNull();
        assertThat(savedTransaction.getAmount()).isEqualTo(new BigDecimal("25.50"));
        assertThat(savedTransaction.getDescription()).isEqualTo("Lunch");
        assertThat(savedTransaction.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(savedTransaction.getCategory().getId()).isEqualTo(expenseCategory.getId());
    }

    @Test
    void findByUserId_shouldReturnUserTransactions() {
        createAndPersistTransaction(testUser, expenseCategory, "50.00", "Groceries", LocalDate.now());
        createAndPersistTransaction(testUser, incomeCategory, "1000.00", "Salary", LocalDate.now());
        createAndPersistTransaction(anotherUser, expenseCategory, "30.00", "Coffee", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findByUserId(testUser.getId());

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder("Groceries", "Salary");
    }

    @Test
    void findByUserId_withPageable_shouldReturnPagedResults() {
        createAndPersistTransaction(testUser, expenseCategory, "50.00", "Transaction 1", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "60.00", "Transaction 2", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "70.00", "Transaction 3", LocalDate.now());

        Pageable pageable = PageRequest.of(0, 2);
        Page<Transaction> transactions = transactionRepository.findByUserId(testUser.getId(), pageable);

        assertThat(transactions.getContent()).hasSize(2);
        assertThat(transactions.getTotalElements()).isEqualTo(3);
        assertThat(transactions.getTotalPages()).isEqualTo(2);
    }

    @Test
    void findByUserIdAndCategoryId_shouldReturnTransactionsForSpecificCategory() {
        createAndPersistTransaction(testUser, expenseCategory, "50.00", "Food expense", LocalDate.now());
        createAndPersistTransaction(testUser, incomeCategory, "1000.00", "Salary income", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "30.00", "Another food expense", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findByUserIdAndCategoryId(
                testUser.getId(), expenseCategory.getId());

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder("Food expense", "Another food expense");
    }

    @Test
    void findByUserIdAndTransactionDateBetween_shouldReturnTransactionsInDateRange() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        createAndPersistTransaction(testUser, expenseCategory, "50.00", "January transaction", LocalDate.of(2023, 1, 15));
        createAndPersistTransaction(testUser, expenseCategory, "60.00", "February transaction", LocalDate.of(2023, 2, 15));
        createAndPersistTransaction(testUser, expenseCategory, "70.00", "Another January transaction", LocalDate.of(2023, 1, 25));

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                testUser.getId(), startDate, endDate);

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder("January transaction", "Another January transaction");
    }

    @Test
    void findByUserIdAndAmountGreaterThan_shouldReturnTransactionsAboveAmount() {
        createAndPersistTransaction(testUser, expenseCategory, "25.00", "Small expense", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "75.00", "Large expense", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "50.00", "Medium expense", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findByUserIdAndAmountGreaterThan(
                testUser.getId(), new BigDecimal("30.00"));

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder("Large expense", "Medium expense");
    }

    @Test
    void findByUserIdAndAmountLessThan_shouldReturnTransactionsBelowAmount() {
        createAndPersistTransaction(testUser, expenseCategory, "25.00", "Small expense", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "75.00", "Large expense", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "50.00", "Medium expense", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findByUserIdAndAmountLessThan(
                testUser.getId(), new BigDecimal("60.00"));

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder("Small expense", "Medium expense");
    }

    @Test
    void findByUserIdAndDescriptionContainingIgnoreCase_shouldReturnMatchingTransactions() {
        createAndPersistTransaction(testUser, expenseCategory, "25.00", "Restaurant lunch", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "75.00", "Grocery shopping", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "50.00", "Fast food dinner", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findByUserIdAndDescriptionContainingIgnoreCase(
                testUser.getId(), "FOOD");

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getDescription()).isEqualTo("Fast food dinner");
    }

    @Test
    void findByUserIdAndDescriptionContainingIgnoreCase_shouldBeCaseInsensitive() {
        createAndPersistTransaction(testUser, expenseCategory, "25.00", "Restaurant Lunch", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findByUserIdAndDescriptionContainingIgnoreCase(
                testUser.getId(), "restaurant");

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getDescription()).isEqualTo("Restaurant Lunch");
    }

    @Test
    void findById_shouldReturnTransactionWhenExists() {
        Transaction transaction = createAndPersistTransaction(testUser, expenseCategory, "50.00", "Test", LocalDate.now());

        Optional<Transaction> found = transactionRepository.findById(transaction.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(transaction.getId());
        assertThat(found.get().getDescription()).isEqualTo("Test");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<Transaction> found = transactionRepository.findById(999L);

        assertThat(found).isNotPresent();
    }

    @Test
    void deleteById_shouldRemoveTransaction() {
        Transaction transaction = createAndPersistTransaction(testUser, expenseCategory, "50.00", "Test", LocalDate.now());
        Long transactionId = transaction.getId();

        transactionRepository.deleteById(transactionId);
        entityManager.flush();

        Optional<Transaction> found = transactionRepository.findById(transactionId);
        assertThat(found).isNotPresent();
    }

    @Test
    void save_shouldUpdateExistingTransaction() {
        Transaction transaction = createAndPersistTransaction(testUser, expenseCategory, "50.00", "Original", LocalDate.now());

        transaction.setAmount(new BigDecimal("75.00"));
        transaction.setDescription("Updated");

        Transaction updatedTransaction = transactionRepository.save(transaction);
        entityManager.flush();

        assertThat(updatedTransaction.getId()).isEqualTo(transaction.getId());
        assertThat(updatedTransaction.getAmount()).isEqualTo(new BigDecimal("75.00"));
        assertThat(updatedTransaction.getDescription()).isEqualTo("Updated");
    }

    @Test
    void findByUserId_shouldReturnEmptyListWhenNoTransactions() {
        List<Transaction> transactions = transactionRepository.findByUserId(999L);
        assertThat(transactions).isEmpty();
    }

    @Test
    void findByUserIdAndCategoryId_shouldReturnEmptyListWhenNoTransactions() {
        List<Transaction> transactions = transactionRepository.findByUserIdAndCategoryId(testUser.getId(), 999L);
        assertThat(transactions).isEmpty();
    }

    @Test
    void findByUserIdAndTransactionDateBetween_shouldHandleBoundaryDates() {
        LocalDate testDate = LocalDate.of(2023, 6, 15);
        createAndPersistTransaction(testUser, expenseCategory, "50.00", "On start date", LocalDate.of(2023, 6, 1));
        createAndPersistTransaction(testUser, expenseCategory, "60.00", "On end date", LocalDate.of(2023, 6, 30));
        createAndPersistTransaction(testUser, expenseCategory, "70.00", "Outside range", LocalDate.of(2023, 7, 1));

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(
                testUser.getId(), LocalDate.of(2023, 6, 1), LocalDate.of(2023, 6, 30));

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder("On start date", "On end date");
    }

    @Test
    void findByUserIdAndAmountGreaterThan_shouldNotIncludeEqualAmount() {
        createAndPersistTransaction(testUser, expenseCategory, "50.00", "Equal amount", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "50.01", "Greater amount", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "49.99", "Lesser amount", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findByUserIdAndAmountGreaterThan(
                testUser.getId(), new BigDecimal("50.00"));

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getDescription()).isEqualTo("Greater amount");
    }

    @Test
    void findByUserIdAndAmountLessThan_shouldNotIncludeEqualAmount() {
        createAndPersistTransaction(testUser, expenseCategory, "50.00", "Equal amount", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "50.01", "Greater amount", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "49.99", "Lesser amount", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findByUserIdAndAmountLessThan(
                testUser.getId(), new BigDecimal("50.00"));

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getDescription()).isEqualTo("Lesser amount");
    }

    @Test
    void findByUserIdAndDescriptionContainingIgnoreCase_shouldReturnEmptyWhenNoMatches() {
        createAndPersistTransaction(testUser, expenseCategory, "25.00", "Restaurant lunch", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findByUserIdAndDescriptionContainingIgnoreCase(
                testUser.getId(), "nonexistent");

        assertThat(transactions).isEmpty();
    }

    @Test
    void findByUserIdAndDescriptionContainingIgnoreCase_shouldHandlePartialMatches() {
        createAndPersistTransaction(testUser, expenseCategory, "25.00", "Supermarket grocery shopping", LocalDate.now());
        createAndPersistTransaction(testUser, expenseCategory, "30.00", "Fast food", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findByUserIdAndDescriptionContainingIgnoreCase(
                testUser.getId(), "market");

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getDescription()).isEqualTo("Supermarket grocery shopping");
    }

    @Test
    void findAll_shouldReturnAllTransactions() {
        createAndPersistTransaction(testUser, expenseCategory, "50.00", "User1 transaction", LocalDate.now());
        createAndPersistTransaction(anotherUser, expenseCategory, "60.00", "User2 transaction", LocalDate.now());

        List<Transaction> transactions = transactionRepository.findAll();

        assertThat(transactions).hasSize(2);
        assertThat(transactions).extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder("User1 transaction", "User2 transaction");
    }

    @Test
    void findByUserId_withPageable_shouldHandleEmptyResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> transactions = transactionRepository.findByUserId(999L, pageable);

        assertThat(transactions.getContent()).isEmpty();
        assertThat(transactions.getTotalElements()).isZero();
        assertThat(transactions.getTotalPages()).isZero();
    }

    @Test
    void save_shouldSetCreatedAtAndUpdatedAt() {
        Transaction transaction = Transaction.builder()
                .user(testUser)
                .category(expenseCategory)
                .amount(new BigDecimal("100.00"))
                .transactionDate(LocalDate.now())
                .description("Test transaction")
                .build();

        Transaction saved = transactionRepository.save(transaction);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(saved.getUpdatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void update_shouldUpdateUpdatedAtField() {
        Transaction transaction = createTestTransaction(
                testUser, expenseCategory, new BigDecimal("100.00"),
                LocalDate.now(), "Original description");

        LocalDateTime originalCreatedAt = transaction.getCreatedAt();
        LocalDateTime originalUpdatedAt = transaction.getUpdatedAt();

        // Update the transaction
        transaction.setDescription("Updated description");
        Transaction updated = transactionRepository.save(transaction);

        assertThat(updated.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
        assertThat(updated.getDescription()).isEqualTo("Updated description");
    }

    private User createAndPersistUser(String username, String email) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password("password123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(user);
    }

    private Category createAndPersistCategory(User user, String name, CategoryType type) {
        Category category = Category.builder()
                .user(user)
                .name(name)
                .type(type)
                .color("#FF0000")
                .createdAt(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(category);
    }

    private Transaction createAndPersistTransaction(User user, Category category, String amount,
                                                  String description, LocalDate date) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(new BigDecimal(amount))
                .description(description)
                .transactionDate(date)
                .createdAt(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(transaction);
    }

    private Transaction createTestTransaction(User user, Category category, BigDecimal amount, LocalDate date, String description) {
        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(amount)
                .transactionDate(date)
                .description(description)
                .build();

        // JPA will automatically call @PrePersist when saving
        return transactionRepository.save(transaction);
    }
}
