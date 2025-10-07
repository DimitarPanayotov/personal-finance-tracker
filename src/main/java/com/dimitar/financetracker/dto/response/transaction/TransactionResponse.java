package com.dimitar.financetracker.dto.response.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema; // added

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Transaction details.")
public class TransactionResponse {
    @Schema(description = "Transaction identifier", example = "5001", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "Owner user ID", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
    private Long userId;
    @Schema(description = "Associated category ID", example = "101", accessMode = Schema.AccessMode.READ_ONLY)
    private Long categoryId;
    @Schema(description = "Associated category name", example = "Groceries", accessMode = Schema.AccessMode.READ_ONLY)
    private String categoryName;
    @Schema(description = "Transaction amount", example = "89.99", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal amount;
    @Schema(description = "Optional description", example = "Weekly grocery run", accessMode = Schema.AccessMode.READ_ONLY)
    private String description;
    @Schema(description = "Date of the transaction (ISO-8601)", example = "2025-10-07", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate transactionDate;
    @Schema(description = "Creation timestamp (UTC)", example = "2025-10-07T10:11:12", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    @Schema(description = "Last update timestamp (UTC)", example = "2025-10-07T11:22:33", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
