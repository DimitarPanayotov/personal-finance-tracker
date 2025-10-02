package com.dimitar.financetracker.service.command.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.entity.User;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTransactionCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;

    private CreateTransactionCommand command;

    @BeforeEach
    void setUp() {
        command = new CreateTransactionCommand(authenticationFacade, categoryRepository, transactionRepository, transactionMapper);
    }

    @Test
    void execute_createsTransaction_whenCategoryBelongsToUser() {
        Long userId = 1L;
        User user = User.builder().id(userId).username("john").build();
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);

        Long categoryId = 10L;
        Category category = Category.builder().id(categoryId).name("Food").type(CategoryType.EXPENSE).build();
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(category));

        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(categoryId)
                .amount(new BigDecimal("12.34"))
                .description(" lunch ")
                .transactionDate(LocalDate.of(2025, 1, 15))
                .build();

        Transaction mapped = Transaction.builder().user(user).category(category).amount(new BigDecimal("12.34")).description("lunch").transactionDate(LocalDate.of(2025, 1, 15)).build();
        when(transactionMapper.toEntity(request, user, category)).thenReturn(mapped);

        when(transactionRepository.save(mapped)).thenReturn(mapped);

        TransactionResponse expected = TransactionResponse.builder().id(999L).userId(userId).categoryId(categoryId).amount(new BigDecimal("12.34")).description("lunch").transactionDate(LocalDate.of(2025,1,15)).build();
        when(transactionMapper.toResponse(mapped)).thenReturn(expected);

        TransactionResponse result = command.execute(request);

        assertEquals(expected, result);
        verify(categoryRepository).findByIdAndUserId(categoryId, userId);
        verify(transactionMapper).toEntity(request, user, category);
        verify(transactionRepository).save(mapped);
        verify(transactionMapper).toResponse(mapped);
        verifyNoMoreInteractions(categoryRepository, transactionRepository, transactionMapper);
    }

    @Test
    void execute_throwsWhenCategoryNotFoundOrNotOwned() {
        Long userId = 2L;
        User user = User.builder().id(userId).username("ann").build();
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);

        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(123L)
                .amount(new BigDecimal("5.00"))
                .transactionDate(LocalDate.now())
                .build();

        when(categoryRepository.findByIdAndUserId(123L, userId)).thenReturn(Optional.empty());

        assertThrows(CategoryDoesNotExistException.class, () -> command.execute(request));
        verify(transactionRepository, never()).save(any());
        verifyNoInteractions(transactionMapper);
    }
}

