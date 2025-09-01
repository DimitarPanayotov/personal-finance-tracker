package com.dimitar.financetracker.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.dimitar.financetracker.util.DatabaseConstants.EMAIL_MAX_LENGTH;
import static com.dimitar.financetracker.util.DatabaseConstants.USERNAME_MAX_LENGTH;
import static com.dimitar.financetracker.util.ErrorMessages.EMAIL_INVALID;
import static com.dimitar.financetracker.util.ErrorMessages.EMAIL_TOO_LONG;
import static com.dimitar.financetracker.util.ErrorMessages.USERNAME_TOO_LONG;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    private Long id;

    @Size(max = USERNAME_MAX_LENGTH, message = USERNAME_TOO_LONG)
    private String username;

    @Size(max = EMAIL_MAX_LENGTH, message = EMAIL_TOO_LONG)
    @Email(message = EMAIL_INVALID)
    private String email;
}
