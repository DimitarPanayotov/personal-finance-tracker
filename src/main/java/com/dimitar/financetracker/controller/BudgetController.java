package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.budget.CreateBudgetRequest;
import com.dimitar.financetracker.dto.request.budget.UpdateBudgetRequest;
import com.dimitar.financetracker.dto.response.budget.BudgetResponse;
import com.dimitar.financetracker.dto.response.budget.BudgetUsageResponse;
import com.dimitar.financetracker.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Budgets", description = "Operations for creating, updating, activating, deactivating, and tracking usage of budgets")
public class BudgetController {
    private final BudgetService budgetService;

    @Operation(
            summary = "Create a budget",
            description = "Creates a new budget for a category or grouping with defined limits and period."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Budget successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed for supplied budget data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "409", description = "Conflict - overlapping or duplicate budget exists")
    })
    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody CreateBudgetRequest request) {
        BudgetResponse response = budgetService.createBudget(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Deactivate a budget",
            description = "Marks a budget as inactive so it no longer tracks usage, without deleting historical data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget successfully deactivated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - budget already inactive")
    })
    @PostMapping("/{budgetId}/deactivate")
    public ResponseEntity<BudgetResponse> deactivateBudget(@PathVariable Long budgetId) {
        BudgetResponse response = budgetService.deactivateBudget(budgetId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Activate a budget",
            description = "Re-activates a previously inactive budget so it resumes usage tracking."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget successfully activated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - budget already active")
    })
    @PostMapping("/{budgetId}/activate")
    public ResponseEntity<BudgetResponse> activateBudget(@PathVariable Long budgetId) {
        BudgetResponse response = budgetService.activateBudget(budgetId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update a budget",
            description = "Updates mutable fields of a budget (e.g., amount, period, category)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed for supplied budget data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - new settings overlap with another budget")
    })
    @PatchMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(@PathVariable Long budgetId,
                                                       @Valid @RequestBody UpdateBudgetRequest request) {
        request.setBudgetId(budgetId);
        BudgetResponse response = budgetService.updateBudget(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a budget",
            description = "Deletes a budget definition. Depending on business logic this may remove or archive it."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Budget successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - budget cannot be deleted due to constraints")
    })
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "List all budgets",
            description = "Retrieves all budgets owned by the authenticated user (active and inactive)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budgets successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets() {
        List<BudgetResponse> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }

    @Operation(
            summary = "List active budgets",
            description = "Retrieves only budgets currently active."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active budgets successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/active")
    public ResponseEntity<List<BudgetResponse>> getActiveBudgets() {
        List<BudgetResponse> budgets = budgetService.getActiveBudgets();
        return ResponseEntity.ok(budgets);
    }

    @Operation(
            summary = "Get budget by ID",
            description = "Retrieves a single budget by its ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Long budgetId) {
        BudgetResponse response = budgetService.getBudgetById(budgetId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List budgets by category",
            description = "Retrieves budgets that apply to a specific category ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budgets successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<BudgetResponse>> getBudgetsByCategory(@PathVariable Long categoryId) {
        List<BudgetResponse> responses = budgetService.getBudgetsByCategory(categoryId);
        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Get budget usage",
            description = "Retrieves current usage metrics (e.g., spent vs. limit) for the specified budget."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget usage successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    @GetMapping("/{budgetId}/usage")
    public ResponseEntity<BudgetUsageResponse> getBudgetUsage(@PathVariable Long budgetId) {
        BudgetUsageResponse response = budgetService.getBudgetUsage(budgetId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List usage for all budgets",
            description = "Retrieves usage metrics for all budgets (e.g., for dashboard aggregation)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Budget usage list successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    })
    @GetMapping("/usage")
    public ResponseEntity<List<BudgetUsageResponse>> getAllBudgetsUsage() {
        List<BudgetUsageResponse> responses = budgetService.getAllBudgetsUsage();
        return ResponseEntity.ok(responses);
    }

}
