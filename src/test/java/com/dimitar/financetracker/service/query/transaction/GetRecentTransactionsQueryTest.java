package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRecentTransactionsQueryTest {

    @Mock private AuthenticationFacade authenticationFacade;
    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionMapper transactionMapper;

    private GetRecentTransactionsQuery query;

    @BeforeEach
    void setUp() { query = new GetRecentTransactionsQuery(authenticationFacade, transactionRepository, transactionMapper); }

    @Test
    void execute_appliesDefaultLimitAndSort_whenLimitNull() {
        Long userId = 5L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Transaction t1 = Transaction.builder().id(1L).build();
        List<Transaction> list = List.of(t1);
        Page<Transaction> page = new PageImpl<>(list, PageRequest.of(0, 10, Sort.by(Sort.Order.desc("transactionDate"), Sort.Order.desc("createdAt"))), 1);
        when(transactionRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(page);

        when(transactionMapper.toResponse(t1)).thenReturn(TransactionResponse.builder().id(1L).build());

        List<TransactionResponse> result = query.execute(null);
        assertEquals(1, result.size());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(transactionRepository).findByUserId(eq(userId), captor.capture());
        Pageable p = captor.getValue();
        assertEquals(10, p.getPageSize());
        assertEquals(0, p.getPageNumber());
        assertTrue(p.getSort().getOrderFor("transactionDate").isDescending());
        assertTrue(p.getSort().getOrderFor("createdAt").isDescending());
    }

    @Test
    void execute_clampsLimitToRange() {
        Long userId = 5L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Transaction t1 = Transaction.builder().id(1L).build();
        when(transactionRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(t1), PageRequest.of(0, 100), 1));

        // Below minimum -> clamp to 1
        query.execute(0);
        // Above maximum -> clamp to 100
        query.execute(1000);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(transactionRepository, times(2)).findByUserId(eq(userId), captor.capture());
        List<Pageable> pageables = captor.getAllValues();
        assertEquals(1, pageables.get(0).getPageSize());
        assertEquals(100, pageables.get(1).getPageSize());
    }
}

