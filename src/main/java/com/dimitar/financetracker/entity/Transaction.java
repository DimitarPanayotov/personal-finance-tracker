package com.dimitar.financetracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.dimitar.financetracker.util.DatabaseConstants.AMOUNT_PRECISION;
import static com.dimitar.financetracker.util.DatabaseConstants.AMOUNT_SCALE;
import static com.dimitar.financetracker.util.DatabaseConstants.DESCRIPTION_MAX_LENGTH;
import static com.dimitar.financetracker.util.ErrorMessages.AMOUNT_MIN;
import static com.dimitar.financetracker.util.ErrorMessages.AMOUNT_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.DESCRIPTION_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.TRANSACTION_DATE_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.USER_REQUIRED;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotNull(message = USER_REQUIRED)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotNull(message = CATEGORY_REQUIRED)
    private Category category;

    @Column(nullable = false, precision = AMOUNT_PRECISION, scale = AMOUNT_SCALE)
    @NotNull(message = AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = AMOUNT_MIN)
    private BigDecimal amount;

    @Column
    @Size(max = DESCRIPTION_MAX_LENGTH, message = DESCRIPTION_TOO_LONG)
    private String description;

    @Column(name = "transaction_date", nullable = false)
    @NotNull(message = TRANSACTION_DATE_REQUIRED)
    private LocalDate transactionDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
