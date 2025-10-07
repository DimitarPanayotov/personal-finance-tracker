package com.dimitar.financetracker.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema; // added

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User profile data returned to clients.")
public class UserResponse {
    @Schema(description = "Unique user identifier", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Schema(description = "User's username", example = "john_doe", accessMode = Schema.AccessMode.READ_ONLY)
    private String username;
    @Schema(description = "User's email address", example = "john.doe@example.com", accessMode = Schema.AccessMode.READ_ONLY)
    private String email;
    @Schema(description = "Timestamp when the user was created (UTC)", example = "2025-10-01T12:34:56", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
    @Schema(description = "Timestamp when the user was last updated (UTC)", example = "2025-10-07T08:15:30", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}
