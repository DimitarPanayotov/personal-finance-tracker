package com.dimitar.financetracker.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication result containing access token and related metadata.")
public class AuthenticationResponse {
    @Schema(description = "JWT access token (use in Authorization header: 'Bearer {token}')", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", accessMode = Schema.AccessMode.READ_ONLY)
    private String token;
    @Schema(description = "Token type (always 'Bearer')", example = "Bearer", accessMode = Schema.AccessMode.READ_ONLY)
    private String type = "Bearer";
    @Schema(description = "Authenticated username", example = "john_doe", accessMode = Schema.AccessMode.READ_ONLY)
    private String username;
    @Schema(description = "User email address", example = "john.doe@example.com", accessMode = Schema.AccessMode.READ_ONLY)
    private String email;
    @Schema(description = "Seconds until the token expires", example = "3600", accessMode = Schema.AccessMode.READ_ONLY)
    private Long expiresIn;
}
