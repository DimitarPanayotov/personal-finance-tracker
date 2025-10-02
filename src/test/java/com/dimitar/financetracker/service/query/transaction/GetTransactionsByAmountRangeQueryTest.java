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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTransactionsByAmountRangeQueryTest {

    @Mock private AuthenticationFacade authenticationFacade;
    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionMapper transactionMapper;

    private GetTransactionsByAmountRangeQuery query;

    @BeforeEach
    void setUp() { query = new GetTransactionsByAmountRangeQuery(authenticationFacade, transactionRepository, transactionMapper); }

    @Test
    void execute_betweenRange_swapsWhenMinGreaterThanMax() {
        Long userId = 4L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Transaction t = Transaction.builder().id(1L).build();
        when(transactionRepository.findByUserIdAndAmountBetween(eq(userId), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(List.of(t));
        when(transactionMapper.toResponse(t)).thenReturn(TransactionResponse.builder().id(1L).build());

        List<TransactionResponse> result = query.execute(new GetTransactionsByAmountRangeQuery.AmountRange(new BigDecimal("100.00"), new BigDecimal("10.00")));
        assertEquals(1, result.size());

        ArgumentCaptor<BigDecimal> minCap = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> maxCap = ArgumentCaptor.forClass(BigDecimal.class);
        verify(transactionRepository).findByUserIdAndAmountBetween(eq(userId), minCap.capture(), maxCap.capture());
        assertEquals(new BigDecimal("10.00"), minCap.getValue());
        assertEquals(new BigDecimal("100.00"), maxCap.getValue());
    }

    @Test
    void execute_minOnly_callsGte() {
        Long userId = 4L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Transaction t = Transaction.builder().id(1L).build();
        when(transactionRepository.findByUserIdAndAmountGreaterThanEqual(userId, new BigDecimal("5.00"))).thenReturn(List.of(t));
        when(transactionMapper.toResponse(t)).thenReturn(TransactionResponse.builder().id(1L).build());

        List<TransactionResponse> result = query.execute(new GetTransactionsByAmountRangeQuery.AmountRange(new BigDecimal("5.00"), null));
        assertEquals(1, result.size());
        verify(transactionRepository).findByUserIdAndAmountGreaterThanEqual(userId, new BigDecimal("5.00"));
    }

    @Test
    void execute_maxOnly_callsLte() {
        Long userId = 4L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Transaction t = Transaction.builder().id(1L).build();
        when(transactionRepository.findByUserIdAndAmountLessThanEqual(userId, new BigDecimal("20.00"))).thenReturn(List.of(t));
        when(transactionMapper.toResponse(t)).thenReturn(TransactionResponse.builder().id(1L).build());

        List<TransactionResponse> result = query.execute(new GetTransactionsByAmountRangeQuery.AmountRange(null, new BigDecimal("20.00")));
        assertEquals(1, result.size());
        verify(transactionRepository).findByUserIdAndAmountLessThanEqual(userId, new BigDecimal("20.00"));
    }

    @Test
    void execute_throwsWhenBothNull() {
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(1L);
        assertThrows(IllegalArgumentException.class, () -> query.execute(new GetTransactionsByAmountRangeQuery.AmountRange(null, null)));
    }
}

