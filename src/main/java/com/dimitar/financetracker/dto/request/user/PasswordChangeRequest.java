package com.dimitar.financetracker.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import static com.dimitar.financetracker.util.DatabaseConstants.PASSWORD_MIN_LENGTH;
import static com.dimitar.financetracker.util.ErrorMessages.CURRENT_PASSWORD_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.PASSWORD_REQUIRED;
import static com.dimitar.financetracker.util.ErrorMessages.PASSWORD_TOO_SHORT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload to change the currently authenticated user's password.")
public class PasswordChangeRequest {
    @NotBlank(message = CURRENT_PASSWORD_REQUIRED)
    @Schema(description = "Current (existing) password", example = "OldP@ssw0rd!", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @NotBlank(message = PASSWORD_REQUIRED)
    @Size(min = PASSWORD_MIN_LENGTH, message = PASSWORD_TOO_SHORT)
    @Schema(description = "New password that will replace the current one", example = "N3wStr0ngP@ss!", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String newPassword;

}
