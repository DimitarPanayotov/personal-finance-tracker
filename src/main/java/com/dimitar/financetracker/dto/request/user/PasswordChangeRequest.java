package com.dimitar.financetracker.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.dimitar.financetracker.util.DatabaseConstants.PASSWORD_MIN_LENGTH;
import static com.dimitar.financetracker.util.ErrorMessages.CURRENT_PASSWORD_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.PASSWORD_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.PASSWORD_TOO_SHORT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeRequest {
    private Long userId;

    @NotBlank(message = CURRENT_PASSWORD_REQUIRED)
    private String password;

    @NotBlank(message = PASSWORD_REQUIRED)
    @Size(min = PASSWORD_MIN_LENGTH, message = PASSWORD_TOO_SHORT)
    private String newPassword;

}
