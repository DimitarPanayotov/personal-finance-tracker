package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.dto.response.budget.BudgetUsageResponse;
import com.dimitar.financetracker.service.BudgetService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody CreateBudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets() {
        List<BudgetResponse> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BudgetResponse>> getActiveBudgets() {
        List<BudgetResponse> budgets = budgetService.getActiveBudgets();
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Long budgetId) {
        BudgetResponse response = budgetService.getBudgetById(budgetId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByCategory(@PathVariable Long categoryId) {
        List<BudgetResponse> responses = budgetService.getBudgetsByCategory(categoryId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{budgetId}/usage")
    public ResponseEntity<BudgetUsageResponse> getBudgetUsage(@PathVariable Long budgetId) {
        BudgetUsageResponse response = budgetService.getBudgetUsage(budgetId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usage")
    public ResponseEntity<List<BudgetUsageResponse>> getAllBudgetsUsage() {
        List<BudgetUsageResponse> responses = budgetService.getAllBudgetsUsage();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(@PathVariable Long budgetId,
                                                       @Valid @RequestBody UpdateBudgetRequest request) {
        request.setBudgetId(budgetId);
        BudgetResponse response = budgetService.updateBudget(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{budgetId}/deactivate")
    public ResponseEntity<BudgetResponse> deactivateBudget(@PathVariable Long budgetId) {
        BudgetResponse response = budgetService.deactivateBudget(budgetId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{budgetId}/activate")
    public ResponseEntity<BudgetResponse> activateBudget(@PathVariable Long budgetId) {
        BudgetResponse response = budgetService.activateBudget(budgetId);
        return ResponseEntity.ok(response);
    }
}
