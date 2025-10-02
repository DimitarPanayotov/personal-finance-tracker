package com.dimitar.financetracker.service.command.category;

import com.dimitar.financetracker.dto.request.category.MergeCategoriesRequest;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MergeCategoriesCommandTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AuthenticationFacade authenticationFacade;

    private MergeCategoriesCommand command;

    @BeforeEach
    void setUp() {
        command = new MergeCategoriesCommand(categoryRepository, transactionRepository, authenticationFacade);
    }

    @Test
    void execute_mergesCategories_transfersTransactions_andDeletesSources() {
        Long userId = 1L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Long targetId = 10L;
        Long s1Id = 11L;
        Long s2Id = 12L;

        Category target = Category.builder().id(targetId).type(CategoryType.EXPENSE).build();
        Category s1 = Category.builder().id(s1Id).type(CategoryType.EXPENSE).build();
        Category s2 = Category.builder().id(s2Id).type(CategoryType.EXPENSE).build();

        when(categoryRepository.findByIdAndUserId(targetId, userId)).thenReturn(Optional.of(target));
        when(categoryRepository.findByIdAndUserId(s1Id, userId)).thenReturn(Optional.of(s1));
        when(categoryRepository.findByIdAndUserId(s2Id, userId)).thenReturn(Optional.of(s2));

        Transaction t1 = Transaction.builder().id(100L).user(null).category(s1).amount(new BigDecimal("5.00")).transactionDate(LocalDate.now()).build();
        Transaction t2 = Transaction.builder().id(101L).user(null).category(s1).amount(new BigDecimal("7.00")).transactionDate(LocalDate.now()).build();
        Transaction t3 = Transaction.builder().id(102L).user(null).category(s2).amount(new BigDecimal("9.00")).transactionDate(LocalDate.now()).build();

        when(transactionRepository.findByUserIdAndCategoryId(userId, s1Id)).thenReturn(List.of(t1, t2));
        when(transactionRepository.findByUserIdAndCategoryId(userId, s2Id)).thenReturn(List.of(t3));

        MergeCategoriesRequest request = MergeCategoriesRequest.builder()
                .targetCategoryId(targetId)
                .sourceCategoryIds(List.of(s1Id, s2Id))
                .build();

        command.execute(request);

        // Assert each transaction was moved to target and saved
        assertEquals(target, t1.getCategory());
        assertEquals(target, t2.getCategory());
        assertEquals(target, t3.getCategory());
        verify(transactionRepository).save(t1);
        verify(transactionRepository).save(t2);
        verify(transactionRepository).save(t3);

        // Sources are deleted at the end
        verify(categoryRepository).deleteAll(List.of(s1, s2));
    }

    @Test
    void execute_throwsWhenTargetNotFound() {
        Long userId = 1L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(categoryRepository.findByIdAndUserId(10L, userId)).thenReturn(Optional.empty());

        MergeCategoriesRequest request = MergeCategoriesRequest.builder()
                .targetCategoryId(10L)
                .sourceCategoryIds(List.of(11L))
                .build();

        assertThrows(CategoryDoesNotExistException.class, () -> command.execute(request));
        verifyNoMoreInteractions(categoryRepository);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void execute_throwsWhenSourceHasDifferentType() {
        Long userId = 1L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Category target = Category.builder().id(10L).type(CategoryType.EXPENSE).build();
        Category incomeSource = Category.builder().id(11L).type(CategoryType.INCOME).build();

        when(categoryRepository.findByIdAndUserId(10L, userId)).thenReturn(Optional.of(target));
        when(categoryRepository.findByIdAndUserId(11L, userId)).thenReturn(Optional.of(incomeSource));

        MergeCategoriesRequest request = MergeCategoriesRequest.builder()
                .targetCategoryId(10L)
                .sourceCategoryIds(List.of(11L))
                .build();

        assertThrows(IllegalArgumentException.class, () -> command.execute(request));
        verify(transactionRepository, never()).save(any());
        verify(categoryRepository, never()).deleteAll(any());
    }

    @Test
    void execute_throwsWhenSourceIsSameAsTarget() {
        Long userId = 1L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Category target = Category.builder().id(10L).type(CategoryType.EXPENSE).build();
        when(categoryRepository.findByIdAndUserId(10L, userId)).thenReturn(Optional.of(target));
        when(categoryRepository.findByIdAndUserId(10L, userId)).thenReturn(Optional.of(target));

        MergeCategoriesRequest request = MergeCategoriesRequest.builder()
                .targetCategoryId(10L)
                .sourceCategoryIds(List.of(10L))
                .build();

        assertThrows(IllegalArgumentException.class, () -> command.execute(request));
        verify(transactionRepository, never()).save(any());
        verify(categoryRepository, never()).deleteAll(any());
    }
}
