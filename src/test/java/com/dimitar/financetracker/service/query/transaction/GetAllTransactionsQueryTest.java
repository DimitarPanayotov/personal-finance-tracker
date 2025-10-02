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
class GetAllTransactionsQueryTest {

    @Mock private AuthenticationFacade authenticationFacade;
    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionMapper transactionMapper;

    private GetAllTransactionsQuery query;

    @BeforeEach
    void setUp() { query = new GetAllTransactionsQuery(authenticationFacade, transactionRepository, transactionMapper); }

    @Test
    void execute_returnsMappedTransactionsForAuthenticatedUser() {
        Long userId = 1L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Transaction t1 = Transaction.builder().id(1L).build();
        Transaction t2 = Transaction.builder().id(2L).build();
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(t1, t2));

        TransactionResponse r1 = TransactionResponse.builder().id(1L).build();
        TransactionResponse r2 = TransactionResponse.builder().id(2L).build();
        when(transactionMapper.toResponse(t1)).thenReturn(r1);
        when(transactionMapper.toResponse(t2)).thenReturn(r2);

        List<TransactionResponse> result = query.execute(null);

        assertEquals(List.of(r1, r2), result);
        verify(transactionRepository).findByUserId(userId);
        verify(transactionMapper).toResponse(t1);
        verify(transactionMapper).toResponse(t2);
        verifyNoMoreInteractions(transactionRepository, transactionMapper);
    }
}

