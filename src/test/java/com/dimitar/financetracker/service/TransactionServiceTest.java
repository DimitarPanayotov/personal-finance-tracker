package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.PageRequest;
import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.request.transaction.UpdateTransactionRequest;
import com.dimitar.financetracker.dto.response.PagedResponse;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.service.command.transaction.CreateTransactionCommand;
import com.dimitar.financetracker.service.command.transaction.DeleteTransactionCommand;
import com.dimitar.financetracker.service.command.transaction.DuplicateTransactionCommand;
import com.dimitar.financetracker.service.command.transaction.UpdateTransactionCommand;
import com.dimitar.financetracker.service.query.transaction.GetAllTransactionsQuery;
import com.dimitar.financetracker.service.query.transaction.GetRecentTransactionsQuery;
import com.dimitar.financetracker.service.query.transaction.GetTransactionByIdQuery;
import com.dimitar.financetracker.service.query.transaction.GetTransactionsByAmountRangeQuery;
import com.dimitar.financetracker.service.query.transaction.GetTransactionsByCategoryQuery;
import com.dimitar.financetracker.service.query.transaction.GetTransactionsInDateRangeQuery;
import com.dimitar.financetracker.service.query.transaction.SearchTransactionsByDescriptionQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private CreateTransactionCommand createTransactionCommand;
    @Mock private GetAllTransactionsQuery getAllTransactionsQuery;
    @Mock private GetTransactionByIdQuery getTransactionByIdQuery;
    @Mock private GetTransactionsInDateRangeQuery getTransactionsInDateRangeQuery;
    @Mock private UpdateTransactionCommand updateTransactionCommand;
    @Mock private DeleteTransactionCommand deleteTransactionCommand;
    @Mock private DuplicateTransactionCommand duplicateTransactionCommand;
    @Mock private GetTransactionsByCategoryQuery getTransactionsByCategoryQuery;
    @Mock private GetTransactionsByAmountRangeQuery getTransactionsByAmountRangeQuery;
    @Mock private SearchTransactionsByDescriptionQuery searchTransactionsByDescriptionQuery;
    @Mock private GetRecentTransactionsQuery getRecentTransactionsQuery;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(
                createTransactionCommand,
                getAllTransactionsQuery,
                getTransactionByIdQuery,
                getTransactionsInDateRangeQuery,
                updateTransactionCommand,
                deleteTransactionCommand,
                duplicateTransactionCommand,
                getTransactionsByCategoryQuery,
                getTransactionsByAmountRangeQuery,
                searchTransactionsByDescriptionQuery,
                getRecentTransactionsQuery
        );
    }

    @Test
    void createTransaction_delegatesToCommand() {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .categoryId(1L)
                .amount(BigDecimal.TEN)
                .description("Lunch")
                .transactionDate(LocalDate.now())
                .build();
        TransactionResponse expected = TransactionResponse.builder().id(100L).amount(BigDecimal.TEN).build();
        when(createTransactionCommand.execute(request)).thenReturn(expected);

        TransactionResponse actual = transactionService.createTransaction(request);

        assertEquals(expected, actual);
        verify(createTransactionCommand).execute(request);
        verifyNoMoreInteractions(createTransactionCommand);
        verifyNoInteractions(getAllTransactionsQuery, getTransactionByIdQuery, getTransactionsInDateRangeQuery,
                updateTransactionCommand, deleteTransactionCommand, duplicateTransactionCommand,
                getTransactionsByCategoryQuery, getTransactionsByAmountRangeQuery, searchTransactionsByDescriptionQuery,
                getRecentTransactionsQuery);
    }

    @Test
    void getAllTransactions_delegatesToQuery() {
        PageRequest pageRequest = PageRequest.builder()
                .page(0)
                .size(20)
                .sortBy("transactionDate")
                .sortDirection("DESC")
                .build();

        PagedResponse<TransactionResponse> expected = PagedResponse.<TransactionResponse>builder()
                .content(List.of(
                        TransactionResponse.builder().id(1L).build(),
                        TransactionResponse.builder().id(2L).build()
                ))
                .pageNumber(0)
                .pageSize(20)
                .totalElements(2)
                .totalPages(1)
                .first(true)
                .last(true)
                .empty(false)
                .build();

        when(getAllTransactionsQuery.execute(pageRequest)).thenReturn(expected);

        PagedResponse<TransactionResponse> actual = transactionService.getAllTransactions(pageRequest);

        assertEquals(expected, actual);
        verify(getAllTransactionsQuery).execute(pageRequest);
        verifyNoMoreInteractions(getAllTransactionsQuery);
        verifyNoInteractions(createTransactionCommand, getTransactionByIdQuery, getTransactionsInDateRangeQuery,
                updateTransactionCommand, deleteTransactionCommand, duplicateTransactionCommand,
                getTransactionsByCategoryQuery, getTransactionsByAmountRangeQuery, searchTransactionsByDescriptionQuery,
                getRecentTransactionsQuery);
    }

    @Test
    void getTransactionById_delegatesToQuery() {
        Long id = 5L;
        TransactionResponse expected = TransactionResponse.builder().id(id).build();
        when(getTransactionByIdQuery.execute(id)).thenReturn(expected);

        TransactionResponse actual = transactionService.getTransactionById(id);

        assertEquals(expected, actual);
        verify(getTransactionByIdQuery).execute(id);
        verifyNoMoreInteractions(getTransactionByIdQuery);
        verifyNoInteractions(createTransactionCommand, getAllTransactionsQuery, getTransactionsInDateRangeQuery,
                updateTransactionCommand, deleteTransactionCommand, duplicateTransactionCommand,
                getTransactionsByCategoryQuery, getTransactionsByAmountRangeQuery, searchTransactionsByDescriptionQuery,
                getRecentTransactionsQuery);
    }

    @Test
    void getTransactionsInDateRange_delegatesToQueryWithRecord() {
        LocalDate start = LocalDate.of(2025, 1, 10);
        LocalDate end = LocalDate.of(2025, 2, 1);
        List<TransactionResponse> expected = List.of(TransactionResponse.builder().id(1L).build());
        when(getTransactionsInDateRangeQuery.execute(new GetTransactionsInDateRangeQuery.DateRange(start, end)))
                .thenReturn(expected);

        List<TransactionResponse> actual = transactionService.getTransactionsInDateRange(start, end);

        assertEquals(expected, actual);
        verify(getTransactionsInDateRangeQuery)
                .execute(new GetTransactionsInDateRangeQuery.DateRange(start, end));
        verifyNoMoreInteractions(getTransactionsInDateRangeQuery);
        verifyNoInteractions(createTransactionCommand, getAllTransactionsQuery, getTransactionByIdQuery,
                updateTransactionCommand, deleteTransactionCommand, duplicateTransactionCommand,
                getTransactionsByCategoryQuery, getTransactionsByAmountRangeQuery, searchTransactionsByDescriptionQuery,
                getRecentTransactionsQuery);
    }

    @Test
    void updateTransaction_delegatesToCommand() {
        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .transactionId(1L)
                .amount(BigDecimal.valueOf(12.34))
                .description("Updated")
                .build();
        TransactionResponse expected = TransactionResponse.builder().id(1L).amount(BigDecimal.valueOf(12.34)).build();
        when(updateTransactionCommand.execute(request)).thenReturn(expected);

        TransactionResponse actual = transactionService.updateTransaction(request);

        assertEquals(expected, actual);
        verify(updateTransactionCommand).execute(request);
        verifyNoMoreInteractions(updateTransactionCommand);
        verifyNoInteractions(createTransactionCommand, getAllTransactionsQuery, getTransactionByIdQuery,
                getTransactionsInDateRangeQuery, deleteTransactionCommand, duplicateTransactionCommand,
                getTransactionsByCategoryQuery, getTransactionsByAmountRangeQuery, searchTransactionsByDescriptionQuery,
                getRecentTransactionsQuery);
    }

    @Test
    void deleteTransaction_delegatesToCommand() {
        Long id = 7L;
        transactionService.deleteTransaction(id);
        verify(deleteTransactionCommand).execute(id);
        verifyNoMoreInteractions(deleteTransactionCommand);
        verifyNoInteractions(createTransactionCommand, getAllTransactionsQuery, getTransactionByIdQuery,
                getTransactionsInDateRangeQuery, updateTransactionCommand, duplicateTransactionCommand,
                getTransactionsByCategoryQuery, getTransactionsByAmountRangeQuery, searchTransactionsByDescriptionQuery,
                getRecentTransactionsQuery);
    }

    @Test
    void duplicateTransaction_delegatesToCommand() {
        Long id = 11L;
        TransactionResponse expected = TransactionResponse.builder().id(99L).build();
        when(duplicateTransactionCommand.execute(id)).thenReturn(expected);

        TransactionResponse actual = transactionService.duplicateTransaction(id);

        assertEquals(expected, actual);
        verify(duplicateTransactionCommand).execute(id);
        verifyNoMoreInteractions(duplicateTransactionCommand);
        verifyNoInteractions(createTransactionCommand, getAllTransactionsQuery, getTransactionByIdQuery,
                getTransactionsInDateRangeQuery, updateTransactionCommand, deleteTransactionCommand,
                getTransactionsByCategoryQuery, getTransactionsByAmountRangeQuery, searchTransactionsByDescriptionQuery,
                getRecentTransactionsQuery);
    }

    @Test
    void getTransactionsByCategory_delegatesToQuery() {
        Long categoryId = 3L;
        List<TransactionResponse> expected = List.of(TransactionResponse.builder().id(1L).build());
        when(getTransactionsByCategoryQuery.execute(categoryId)).thenReturn(expected);

        List<TransactionResponse> actual = transactionService.getTransactionsByCategory(categoryId);

        assertEquals(expected, actual);
        verify(getTransactionsByCategoryQuery).execute(categoryId);
        verifyNoMoreInteractions(getTransactionsByCategoryQuery);
        verifyNoInteractions(createTransactionCommand, getAllTransactionsQuery, getTransactionByIdQuery,
                getTransactionsInDateRangeQuery, updateTransactionCommand, deleteTransactionCommand,
                duplicateTransactionCommand, getTransactionsByAmountRangeQuery, searchTransactionsByDescriptionQuery,
                getRecentTransactionsQuery);
    }

    @Test
    void getTransactionsByAmountRange_delegatesToQueryWithRecord() {
        BigDecimal min = BigDecimal.valueOf(5);
        BigDecimal max = BigDecimal.valueOf(50);
        List<TransactionResponse> expected = List.of(TransactionResponse.builder().id(1L).build());
        when(getTransactionsByAmountRangeQuery.execute(new GetTransactionsByAmountRangeQuery.AmountRange(min, max)))
                .thenReturn(expected);

        List<TransactionResponse> actual = transactionService.getTransactionsByAmountRange(min, max);

        assertEquals(expected, actual);
        verify(getTransactionsByAmountRangeQuery)
                .execute(new GetTransactionsByAmountRangeQuery.AmountRange(min, max));
        verifyNoMoreInteractions(getTransactionsByAmountRangeQuery);
        verifyNoInteractions(createTransactionCommand, getAllTransactionsQuery, getTransactionByIdQuery,
                getTransactionsInDateRangeQuery, updateTransactionCommand, deleteTransactionCommand,
                duplicateTransactionCommand, getTransactionsByCategoryQuery, searchTransactionsByDescriptionQuery,
                getRecentTransactionsQuery);
    }

    @Test
    void searchTransactionsByDescription_delegatesToQuery() {
        String q = "groceries";
        List<TransactionResponse> expected = List.of(TransactionResponse.builder().id(1L).build());
        when(searchTransactionsByDescriptionQuery.execute(q)).thenReturn(expected);

        List<TransactionResponse> actual = transactionService.searchTransactionsByDescription(q);

        assertEquals(expected, actual);
        verify(searchTransactionsByDescriptionQuery).execute(q);
        verifyNoMoreInteractions(searchTransactionsByDescriptionQuery);
        verifyNoInteractions(createTransactionCommand, getAllTransactionsQuery, getTransactionByIdQuery,
                getTransactionsInDateRangeQuery, updateTransactionCommand, deleteTransactionCommand,
                duplicateTransactionCommand, getTransactionsByCategoryQuery, getTransactionsByAmountRangeQuery,
                getRecentTransactionsQuery);
    }

    @Test
    void getRecentTransactions_delegatesToQuery() {
        Integer limit = 10;
        List<TransactionResponse> expected = List.of(TransactionResponse.builder().id(1L).build());
        when(getRecentTransactionsQuery.execute(limit)).thenReturn(expected);

        List<TransactionResponse> actual = transactionService.getRecentTransactions(limit);

        assertEquals(expected, actual);
        verify(getRecentTransactionsQuery).execute(limit);
        verifyNoMoreInteractions(getRecentTransactionsQuery);
        verifyNoInteractions(createTransactionCommand, getAllTransactionsQuery, getTransactionByIdQuery,
                getTransactionsInDateRangeQuery, updateTransactionCommand, deleteTransactionCommand,
                duplicateTransactionCommand, getTransactionsByCategoryQuery, getTransactionsByAmountRangeQuery,
                searchTransactionsByDescriptionQuery);
    }
}

