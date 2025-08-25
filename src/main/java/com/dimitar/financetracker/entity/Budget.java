package com.dimitar.financetracker.entity;

import com.dimitar.financetracker.model.BudgetPeriod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
import static com.dimitar.financetracker.util.ErrorMessages.BUDGET_AMOUNT_MIN;
import static com.dimitar.financetracker.util.ErrorMessages.BUDGET_AMOUNT_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.BUDGET_PERIOD_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.END_DATE_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.START_DATE_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.USER_REQUIRED;

@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

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
    @NotNull(message = BUDGET_AMOUNT_REQUIRED)
    @DecimalMin(value = "0.01", message = BUDGET_AMOUNT_MIN)
    private BigDecimal amount;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = START_DATE_REQUIRED)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    @NotNull(message = END_DATE_REQUIRED)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = BUDGET_PERIOD_REQUIRED)
    private BudgetPeriod period;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (endDate == null && period != null && startDate != null) {
            this.endDate = calculateEndDate();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private LocalDate calculateEndDate() {
        return switch (period) {
            case WEEKLY -> startDate.plusWeeks(1);
            case MONTHLY -> startDate.plusMonths(1);
            case QUARTERLY -> startDate.plusMonths(3);
            case YEARLY -> startDate.plusYears(1);
            case CUSTOM -> endDate; // For custom, endDate must be provided
        };
    }

    public boolean isCurrentlyActive() {
        LocalDate today = LocalDate.now();
        return isActive && !today.isBefore(startDate) && !today.isAfter(endDate);
    }
}
