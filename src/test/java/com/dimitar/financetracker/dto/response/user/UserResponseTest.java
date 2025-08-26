package com.dimitar.financetracker.dto.response.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    void builder_shouldCreateValidUserResponse() {
        LocalDateTime now = LocalDateTime.now();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyUserResponse() {
        UserResponse response = new UserResponse();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getUsername()).isNull();
        assertThat(response.getEmail()).isNull();
        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateUserResponseWithAllFields() {
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 15, 30);

        UserResponse response = new UserResponse(
                2L,
                "user2",
                "user2@example.com",
                createdAt,
                updatedAt
        );

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getUsername()).isEqualTo("user2");
        assertThat(response.getEmail()).isEqualTo("user2@example.com");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        UserResponse response = new UserResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setId(3L);
        response.setUsername("user3");
        response.setEmail("user3@example.com");
        response.setCreatedAt(now);
        response.setUpdatedAt(now);

        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getUsername()).isEqualTo("user3");
        assertThat(response.getEmail()).isEqualTo("user3@example.com");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void equals_shouldReturnTrueForSameContent() {
        LocalDateTime time = LocalDateTime.now();

        UserResponse response1 = UserResponse.builder()
                .id(1L)
                .username("user")
                .email("user@example.com")
                .createdAt(time)
                .updatedAt(time)
                .build();

        UserResponse response2 = UserResponse.builder()
                .id(1L)
                .username("user")
                .email("user@example.com")
                .createdAt(time)
                .updatedAt(time)
                .build();

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentContent() {
        LocalDateTime time = LocalDateTime.now();

        UserResponse response1 = UserResponse.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .createdAt(time)
                .updatedAt(time)
                .build();

        UserResponse response2 = UserResponse.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .createdAt(time)
                .updatedAt(time)
                .build();

        assertThat(response1).isNotEqualTo(response2);
    }

    @Test
    void toString_shouldContainAllFields() {
        LocalDateTime now = LocalDateTime.now();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .createdAt(now)
                .updatedAt(now)
                .build();

        String toString = response.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("username=testuser");
        assertThat(toString).contains("email=test@example.com");
        assertThat(toString).contains("createdAt=" + now);
        assertThat(toString).contains("updatedAt=" + now);
    }
}

