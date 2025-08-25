package com.dimitar.financetracker.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

import static com.dimitar.financetracker.util.DatabaseConstants.EMAIL_MAX_LENGTH;
import static com.dimitar.financetracker.util.DatabaseConstants.PASSWORD_MIN_LENGTH;
import static com.dimitar.financetracker.util.DatabaseConstants.USERNAME_MAX_LENGTH;
import static com.dimitar.financetracker.util.ErrorMessages.EMAIL_INVALID;
import static com.dimitar.financetracker.util.ErrorMessages.EMAIL_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.EMAIL_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.PASSWORD_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.PASSWORD_TOO_SHORT;
import static com.dimitar.financetracker.util.ErrorMessages.USERNAME_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.USERNAME_TOO_LONG;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = USERNAME_MAX_LENGTH)
    @NotBlank(message = USERNAME_REQUIRED)
    @Size(max = USERNAME_MAX_LENGTH, message = USERNAME_TOO_LONG)
    private String username;

    @Column(nullable = false, unique = true, length = EMAIL_MAX_LENGTH)
    @NotBlank(message = EMAIL_REQUIRED)
    @Email(message = EMAIL_INVALID)
    @Size(max = EMAIL_MAX_LENGTH, message = EMAIL_TOO_LONG)
    private String email;

    @Column(nullable = false)
    @NotBlank(message = PASSWORD_REQUIRED)
    @Size(min = PASSWORD_MIN_LENGTH, message = PASSWORD_TOO_SHORT)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Category> categories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Budget> budgets;

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
