package com.dimitar.financetracker.dto.request.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.dimitar.financetracker.util.ErrorMessages.AMOUNT_MIN;
import static com.dimitar.financetracker.util.ErrorMessages.DESCRIPTION_TOO_LONG;
import static com.dimitar.financetracker.util.DatabaseConstants.DESCRIPTION_MAX_LENGTH;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload to partially update an existing transaction. Send only fields you want to change.")
public class UpdateTransactionRequest {
    @Schema(description = "ID of the transaction being updated (from path, not body)", example = "5501", accessMode = Schema.AccessMode.READ_ONLY)
    private Long transactionId;

    @Schema(description = "New category ID (optional)", example = "101")
    private Long categoryId;

    @DecimalMin(value = "0.01", message = AMOUNT_MIN)
    @Schema(description = "Updated amount (optional, must be >= 0.01 if provided)", example = "89.99")
    private BigDecimal amount;

    @Size(max = DESCRIPTION_MAX_LENGTH, message = DESCRIPTION_TOO_LONG)
    @Schema(description = "Updated description (optional)", example = "Adjusted grocery purchase after discount")
    private String description;

    @Schema(description = "Updated transaction date (optional, ISO-8601)", example = "2025-10-08")
    private LocalDate transactionDate;
}
