package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.request.transaction.UpdateTransactionRequest;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.service.command.transaction.CreateTransactionCommand;
import com.dimitar.financetracker.service.command.transaction.DeleteTransactionCommand;
import com.dimitar.financetracker.service.command.transaction.UpdateTransactionCommand;
import com.dimitar.financetracker.service.query.transaction.GetAllTransactionsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CreateTransactionCommand createTransactionCommand;
    private final GetAllTransactionsQuery getAllTransactionsQuery;
    private final UpdateTransactionCommand updateTransactionCommand;
    private final DeleteTransactionCommand deleteTransactionCommand;

    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        return createTransactionCommand.execute(request);
    }

    public List<TransactionResponse> getAllTransactions() {
        return getAllTransactionsQuery.execute(null);
    }

    public TransactionResponse updateTransaction(UpdateTransactionRequest request) {
        return updateTransactionCommand.execute(request);
    }

    public void deleteTransaction(Long transactionId) {
        deleteTransactionCommand.execute(transactionId);
    }
}
