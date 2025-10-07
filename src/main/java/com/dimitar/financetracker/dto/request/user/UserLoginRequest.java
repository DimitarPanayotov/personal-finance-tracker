package com.dimitar.financetracker.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Login credentials payload. Provide either username or email in the usernameOrEmail field plus the raw password.")
public class UserLoginRequest {
    @NotBlank(message = USERNAME_OR_EMAIL_REQUIRED)
    @Schema(description = "Username or email of the account", example = "john_doe")
    private String usernameOrEmail;

    @NotBlank(message = PASSWORD_REQUIRED)
    @Schema(description = "Plain password (sent over HTTPS, hashed server-side)", example = "Str0ngP@ss!", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
}
