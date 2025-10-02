package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTransactionsByCategoryQueryTest {

    @Mock private AuthenticationFacade authenticationFacade;
    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionMapper transactionMapper;

    private GetTransactionsByCategoryQuery query;

    @BeforeEach
    void setUp() { query = new GetTransactionsByCategoryQuery(authenticationFacade, transactionRepository, transactionMapper); }

    @Test
    void execute_returnsMapped_forGivenCategory() {
        Long userId = 3L; Long categoryId = 7L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Transaction t1 = Transaction.builder().id(1L).build();
        when(transactionRepository.findByUserIdAndCategoryId(userId, categoryId)).thenReturn(List.of(t1));

        TransactionResponse r1 = TransactionResponse.builder().id(1L).build();
        when(transactionMapper.toResponse(t1)).thenReturn(r1);

        List<TransactionResponse> result = query.execute(categoryId);
        assertEquals(List.of(r1), result);
        verify(transactionRepository).findByUserIdAndCategoryId(userId, categoryId);
        verify(transactionMapper).toResponse(t1);
    }
}

