package com.dimitar.financetracker.dto.mapper;

import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.dto.response.user.UserSummaryResponse;
import com.dimitar.financetracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRegistrationRequest request) {
        if (request == null) {
            return null;
        }
        return User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(request.getPassword()) // TODO: This should be encoded by the service layer, not here
            .build();
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }

    public UserSummaryResponse toSummaryResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserSummaryResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .build();
    }

    public void updateEntity(User user, UserUpdateRequest request) {
        if (user == null || request == null) {
            return;
        }

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            user.setUsername(request.getUsername().trim());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(request.getEmail().trim().toLowerCase());
        }
    }

    public void updatePassword(User user, String newEncodedPassword) {
        if (user == null || newEncodedPassword == null) {
            return;
        }
        user.setPassword(newEncodedPassword);
    }
}
