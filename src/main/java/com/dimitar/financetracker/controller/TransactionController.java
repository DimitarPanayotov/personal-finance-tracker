package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.PageRequest;
import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.request.transaction.UpdateTransactionRequest;
import com.dimitar.financetracker.dto.response.PagedResponse;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "CRUD and query operations for user financial transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(
            summary = "Create a transaction",
            description = "Creates a new financial transaction (income or expense) for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed for supplied transaction data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Referenced resource (e.g., category) not found")
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Duplicate an existing transaction",
            description = "Creates a copy of an existing transaction identified by its ID. Useful for repeating similar entries."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Duplicate transaction created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Original transaction not found")
    })
    @PostMapping("/{transactionId}/duplicate")
    public ResponseEntity<TransactionResponse> duplicateTransaction(@PathVariable Long transactionId) {
        TransactionResponse response = transactionService.duplicateTransaction(transactionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update a transaction",
            description = "Updates mutable fields of an existing transaction."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed for supplied transaction data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Long transactionId,
                                                                 @Valid @RequestBody UpdateTransactionRequest request) {
        request.setTransactionId(transactionId);
        TransactionResponse response = transactionService.updateTransaction(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a transaction",
            description = "Deletes a transaction permanently (or marks it deleted depending on business rules)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transaction successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "List all transactions (non-paginated)",
            description = "Retrieves all transactions for the authenticated user as a simple list."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "List all transactions with pagination",
            description = "Retrieves all transactions for the authenticated user with pagination and sorting support. " +
                         "Supports sorting by any transaction field (e.g., transactionDate, amount, id)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping(params = {"page", "size"})
    public ResponseEntity<PagedResponse<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        PageRequest pageRequest = PageRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PagedResponse<TransactionResponse> transactions = transactionService.getAllTransactions(pageRequest);
        return ResponseEntity.ok(transactions);
    }

    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves a single transaction by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long transactionId) {
        TransactionResponse response = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List transactions within a date range",
            description = "Retrieves transactions whose date falls between the provided startDate and endDate (inclusive)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid date range supplied (e.g., startDate after endDate)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsInDateRange(
        @RequestParam("startDate") LocalDate startDate,
        @RequestParam("endDate") LocalDate endDate) {
        List<TransactionResponse> responses = transactionService.getTransactionsInDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "List transactions by category",
            description = "Retrieves all transactions associated with a specific category ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByCategory(@PathVariable Long categoryId) {
        List<TransactionResponse> responses = transactionService.getTransactionsByCategory(categoryId);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "List transactions by amount range",
            description = "Retrieves transactions with amounts within the specified (optional) inclusive min/max bounds."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid amount range supplied (e.g., min > max)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/amount-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAmountRange(
        @RequestParam(value = "minAmount", required = false) BigDecimal minAmount,
        @RequestParam(value = "maxAmount", required = false) BigDecimal maxAmount) {
        List<TransactionResponse> responses = transactionService.getTransactionsByAmountRange(minAmount, maxAmount);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Search transactions by description",
            description = "Performs a case-insensitive search for transactions whose description contains the provided query substring."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned"),
            @ApiResponse(responseCode = "400", description = "Missing or invalid search query parameter 'q'"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponse>> searchTransactions(
        @RequestParam("q") String q) {
        List<TransactionResponse> responses = transactionService.searchTransactionsByDescription(q);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "List recent transactions",
            description = "Retrieves the most recent transactions limited by the optional 'limit' parameter (default may be applied)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recent transactions successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid limit parameter supplied"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(
        @RequestParam(value = "limit", required = false) Integer limit) {
        List<TransactionResponse> responses = transactionService.getRecentTransactions(limit);
        return ResponseEntity.ok(responses);
    }
}
