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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CreateTransactionCommand createTransactionCommand;
    private final GetAllTransactionsQuery getAllTransactionsQuery;
    private final GetTransactionByIdQuery getTransactionByIdQuery;
    private final GetTransactionsInDateRangeQuery getTransactionsInDateRangeQuery;
    private final UpdateTransactionCommand updateTransactionCommand;
    private final DeleteTransactionCommand deleteTransactionCommand;
    private final DuplicateTransactionCommand duplicateTransactionCommand;
    private final GetTransactionsByCategoryQuery getTransactionsByCategoryQuery;
    private final GetTransactionsByAmountRangeQuery getTransactionsByAmountRangeQuery;
    private final SearchTransactionsByDescriptionQuery searchTransactionsByDescriptionQuery;
    private final GetRecentTransactionsQuery getRecentTransactionsQuery;

    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        return createTransactionCommand.execute(request);
    }

    public PagedResponse<TransactionResponse> getAllTransactions(PageRequest pageRequest) {
        return getAllTransactionsQuery.execute(pageRequest);
    }

    public List<TransactionResponse> getAllTransactions() {
        PagedResponse<TransactionResponse> page = getAllTransactions(
            PageRequest.builder().page(0).size(50).sortBy("transactionDate").sortDirection("DESC").build()
        );
        return page.getContent();
    }

    public TransactionResponse getTransactionById(Long transactionId) {
        return getTransactionByIdQuery.execute(transactionId);
    }

    public List<TransactionResponse> getTransactionsInDateRange(LocalDate startDate, LocalDate endDate) {
        return getTransactionsInDateRangeQuery.execute(
            new GetTransactionsInDateRangeQuery.DateRange(startDate, endDate));
    }

    public TransactionResponse updateTransaction(UpdateTransactionRequest request) {
        return updateTransactionCommand.execute(request);
    }

    public void deleteTransaction(Long transactionId) {
        deleteTransactionCommand.execute(transactionId);
    }

    public TransactionResponse duplicateTransaction(Long transactionId) {
        return duplicateTransactionCommand.execute(transactionId);
    }

    public List<TransactionResponse> getTransactionsByCategory(Long categoryId) {
        return getTransactionsByCategoryQuery.execute(categoryId);
    }

    public List<TransactionResponse> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return getTransactionsByAmountRangeQuery.execute(
            new GetTransactionsByAmountRangeQuery.AmountRange(minAmount, maxAmount)
        );
    }

    public List<TransactionResponse> searchTransactionsByDescription(String q) {
        return searchTransactionsByDescriptionQuery.execute(q);
    }

    public List<TransactionResponse> getRecentTransactions(Integer limit) {
        return getRecentTransactionsQuery.execute(limit);
    }
}
