package com.dimitar.financetracker.dto.mapper;

import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.dto.response.user.UserSummaryResponse;
import com.dimitar.financetracker.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
    }

    @Test
    void toEntity_shouldMapFieldsExceptPassword() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        User user = mapper.toEntity(request);

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        // Password must not be set by the mapper; service layer encodes and sets it
        assertThat(user.getPassword()).isNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getCreatedAt()).isNull();
        assertThat(user.getUpdatedAt()).isNull();
    }

    @Test
    void toEntity_withNullRequest_shouldReturnNull() {
        User user = mapper.toEntity(null);
        assertThat(user).isNull();
    }

    @Test
    void toResponse_shouldMapFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserResponse response = mapper.toResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toResponse_withNullUser_shouldReturnNull() {
        UserResponse response = mapper.toResponse(null);
        assertThat(response).isNull();
    }

    @Test
    void toSummaryResponse_shouldMapFieldsCorrectly() {
        User user = User.builder()
                .id(2L)
                .username("summaryuser")
                .email("summary@example.com")
                .build();

        UserSummaryResponse response = mapper.toSummaryResponse(user);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getUsername()).isEqualTo("summaryuser");
        assertThat(response.getEmail()).isEqualTo("summary@example.com");
    }

    @Test
    void toSummaryResponse_withNullUser_shouldReturnNull() {
        UserSummaryResponse response = mapper.toSummaryResponse(null);
        assertThat(response).isNull();
    }

    @Test
    void updateEntity_shouldUpdateUsernameAndEmail() {
        User user = User.builder()
                .username("olduser")
                .email("old@example.com")
                .build();

        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .build();

        mapper.updateEntity(user, request);

        assertThat(user.getUsername()).isEqualTo("newuser");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void updateEntity_shouldTrimAndLowercaseEmail() {
        User user = User.builder()
                .username("user")
                .email("old@example.com")
                .build();

        UserUpdateRequest request = UserUpdateRequest.builder()
                .email("  NEW@EXAMPLE.COM  ")
                .build();

        mapper.updateEntity(user, request);

        assertThat(user.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void updateEntity_shouldTrimUsername() {
        User user = User.builder()
                .username("olduser")
                .email("user@example.com")
                .build();

        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("  newuser  ")
                .build();

        mapper.updateEntity(user, request);

        assertThat(user.getUsername()).isEqualTo("newuser");
    }

    @Test
    void updateEntity_shouldNotUpdateWithNullFields() {
        User user = User.builder()
                .username("originaluser")
                .email("original@example.com")
                .build();

        UserUpdateRequest request = UserUpdateRequest.builder()
                .username(null)
                .email(null)
                .build();

        mapper.updateEntity(user, request);

        assertThat(user.getUsername()).isEqualTo("originaluser");
        assertThat(user.getEmail()).isEqualTo("original@example.com");
    }

    @Test
    void updateEntity_shouldNotUpdateWithEmptyStrings() {
        User user = User.builder()
                .username("originaluser")
                .email("original@example.com")
                .build();

        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("")
                .email("")
                .build();

        mapper.updateEntity(user, request);

        assertThat(user.getUsername()).isEqualTo("originaluser");
        assertThat(user.getEmail()).isEqualTo("original@example.com");
    }

    @Test
    void updateEntity_shouldNotUpdateWithWhitespaceOnlyStrings() {
        User user = User.builder()
                .username("originaluser")
                .email("original@example.com")
                .build();

        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("   ")
                .email("   ")
                .build();

        mapper.updateEntity(user, request);

        assertThat(user.getUsername()).isEqualTo("originaluser");
        assertThat(user.getEmail()).isEqualTo("original@example.com");
    }

    @Test
    void updateEntity_withNullUser_shouldNotThrow() {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .build();

        // Should not throw exception
        mapper.updateEntity(null, request);
    }

    @Test
    void updateEntity_withNullRequest_shouldNotThrow() {
        User user = User.builder()
                .username("user")
                .email("user@example.com")
                .build();

        // Should not throw exception
        mapper.updateEntity(user, null);

        // User should remain unchanged
        assertThat(user.getUsername()).isEqualTo("user");
        assertThat(user.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void updateEntity_withPartialUpdate_shouldOnlyUpdateProvidedFields() {
        User user = User.builder()
                .username("originaluser")
                .email("original@example.com")
                .build();

        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("newuser")
                // email is null
                .build();

        mapper.updateEntity(user, request);

        assertThat(user.getUsername()).isEqualTo("newuser");
        assertThat(user.getEmail()).isEqualTo("original@example.com");
    }
}
