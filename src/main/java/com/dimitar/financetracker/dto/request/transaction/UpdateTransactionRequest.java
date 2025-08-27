package com.dimitar.financetracker.dto.request.transaction;

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
public class UpdateTransactionRequest {
    private Long categoryId;

    @DecimalMin(value = "0.01", message = AMOUNT_MIN)
    private BigDecimal amount;

    @Size(max = DESCRIPTION_MAX_LENGTH, message = DESCRIPTION_TOO_LONG)
    private String description;

    private LocalDate transactionDate;
}
