package com.dimitar.financetracker.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "Payload for updating the currently authenticated user's profile. Send only the fields you want to change.")
public class UserUpdateRequest {
    @Size(max = USERNAME_MAX_LENGTH, message = USERNAME_TOO_LONG)
    @Schema(description = "New username (optional)", example = "new_username_123")
    private String username;

    @Size(max = EMAIL_MAX_LENGTH, message = EMAIL_TOO_LONG)
    @Email(message = EMAIL_INVALID)
    @Schema(description = "New email address (optional)", example = "new.email@example.com")
    private String email;
}
