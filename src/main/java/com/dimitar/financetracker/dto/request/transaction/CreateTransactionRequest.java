package com.dimitar.financetracker.dto.request.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class CreateTransactionRequest {

    @NotNull(message = CATEGORY_REQUIRED)
    private Long categoryId;

    @NotNull(message = AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = AMOUNT_MIN)
    private BigDecimal amount;

    @Size(max = DESCRIPTION_MAX_LENGTH, message = DESCRIPTION_TOO_LONG)
    private String description;

    @NotNull(message = TRANSACTION_DATE_REQUIRED)
    private LocalDate transactionDate;

}
