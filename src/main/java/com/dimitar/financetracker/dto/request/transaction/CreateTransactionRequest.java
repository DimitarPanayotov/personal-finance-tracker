package com.dimitar.financetracker.dto.request.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.dimitar.financetracker.util.DatabaseConstants.DESCRIPTION_MAX_LENGTH;
import static com.dimitar.financetracker.util.ErrorMessages.AMOUNT_MIN;
import static com.dimitar.financetracker.util.ErrorMessages.AMOUNT_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.DESCRIPTION_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.TRANSACTION_DATE_REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload to create a new transaction (income or expense) for the authenticated user.")
public class CreateTransactionRequest {

    @NotNull(message = CATEGORY_REQUIRED)
    @Schema(description = "ID of the category this transaction belongs to", example = "101")
    private Long categoryId;

    @NotNull(message = AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = AMOUNT_MIN)
    @Schema(description = "Positive monetary amount (two decimal precision typical)", example = "123.45")
    private BigDecimal amount;

    @Size(max = DESCRIPTION_MAX_LENGTH, message = DESCRIPTION_TOO_LONG)
    @Schema(description = "Optional free-text description", example = "Weekly groceries at local market")
    private String description;

    @NotNull(message = TRANSACTION_DATE_REQUIRED)
    @Schema(description = "Date the transaction occurred (ISO-8601)", example = "2025-10-07")
    private LocalDate transactionDate;

}
