package com.dimitar.financetracker.entity;

import com.dimitar.financetracker.model.CategoryType;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

import static com.dimitar.financetracker.util.DatabaseConstants.CATEGORY_NAME_MAX_LENGTH;
import static com.dimitar.financetracker.util.DatabaseConstants.COLOR_LENGTH;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_COLOR_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_COLOR_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_NAME_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_NAME_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.CATEGORY_TYPE_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.USER_REQUIRED;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotNull(message = USER_REQUIRED)
    private User user;

    @Column(nullable = false, length = CATEGORY_NAME_MAX_LENGTH)
    @NotBlank(message = CATEGORY_NAME_REQUIRED)
    @Size(max = CATEGORY_NAME_MAX_LENGTH, message = CATEGORY_NAME_TOO_LONG)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = CATEGORY_TYPE_REQUIRED)
    private CategoryType type;

    @Column(nullable = false, length = COLOR_LENGTH)
    @NotBlank(message = CATEGORY_COLOR_REQUIRED)
    @Size(max = COLOR_LENGTH, message = CATEGORY_COLOR_TOO_LONG)
    private String color;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
