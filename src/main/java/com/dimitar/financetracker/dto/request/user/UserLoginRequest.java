package com.dimitar.financetracker.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.dimitar.financetracker.util.ErrorMessages.PASSWORD_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.USERNAME_OR_EMAIL_REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {
    @NotBlank(message = USERNAME_OR_EMAIL_REQUIRED)
    private String usernameOrEmail;

    @NotBlank(message = PASSWORD_REQUIRED)
    private String password;
}
