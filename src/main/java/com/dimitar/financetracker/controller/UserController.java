package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.user.PasswordChangeRequest;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.dto.response.user.UserStatisticsResponse;
import com.dimitar.financetracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Operations related to the authenticated user profile and statistics")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Update current user profile",
            description = "Updates profile details (e.g., name, email, or other editable fields) of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed for the provided user data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication is required or has expired"),
            @ApiResponse(responseCode = "409", description = "Conflict - unique constraint violation (e.g., email already in use)")
    })
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Change current user password",
            description = "Changes the password of the authenticated user after validating the current password and applying password policy rules."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password successfully changed"),
            @ApiResponse(responseCode = "400", description = "Validation failed (e.g., password criteria not met or current password incorrect)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication is required or has expired")
    })
    @PatchMapping("/me/change-password")
    public ResponseEntity<UserResponse> changeCurrentUserPassword(@Valid @RequestBody PasswordChangeRequest request) {
        UserResponse response = userService.changePassword(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Delete current user account",
            description = "Deletes the authenticated user's account. Depending on implementation this may be a soft or hard delete."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User account successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication is required or has expired")
    })
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        userService.deleteUser();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile information of the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication is required or has expired")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse response = userService.getUser();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get current user statistics",
            description = "Returns aggregated financial statistics for the authenticated user (e.g., spending, budgets, categories)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User statistics successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication is required or has expired")
    })
    @GetMapping("/me/statistics")
    public ResponseEntity<UserStatisticsResponse> getCurrentUserStatistics() {
        UserStatisticsResponse response = userService.getStatistics();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
