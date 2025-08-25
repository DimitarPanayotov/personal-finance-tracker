package com.dimitar.financetracker.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequest {
    @NotBlank(message = USERNAME_REQUIRED)
    @Size(max = USERNAME_MAX_LENGTH, message = USERNAME_TOO_LONG)
    private String username;

    @NotBlank(message = EMAIL_REQUIRED)
    @Email(message = EMAIL_INVALID)
    @Size(max = EMAIL_MAX_LENGTH, message = EMAIL_TOO_LONG)
    private String email;

    @NotBlank(message = PASSWORD_REQUIRED)
    @Size(min = PASSWORD_MIN_LENGTH, message = PASSWORD_TOO_SHORT)
    private String password;

}
