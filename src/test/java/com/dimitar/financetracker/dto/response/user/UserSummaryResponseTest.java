package com.dimitar.financetracker.dto.response.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserSummaryResponseTest {

    @Test
    void builder_shouldCreateValidUserSummaryResponse() {
        UserSummaryResponse response = UserSummaryResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void noArgsConstructor_shouldCreateEmptyUserSummaryResponse() {
        UserSummaryResponse response = new UserSummaryResponse();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getUsername()).isNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void allArgsConstructor_shouldCreateUserSummaryResponseWithAllFields() {
        UserSummaryResponse response = new UserSummaryResponse(
                2L,
                "user2",
                "user2@example.com"
        );

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getUsername()).isEqualTo("user2");
        assertThat(response.getEmail()).isEqualTo("user2@example.com");
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        UserSummaryResponse response = new UserSummaryResponse();

        response.setId(3L);
        response.setUsername("user3");
        response.setEmail("user3@example.com");

        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getUsername()).isEqualTo("user3");
        assertThat(response.getEmail()).isEqualTo("user3@example.com");
    }

    @Test
    void equals_shouldReturnTrueForSameContent() {
        UserSummaryResponse response1 = UserSummaryResponse.builder()
                .id(1L)
                .username("user")
                .email("user@example.com")
                .build();

        UserSummaryResponse response2 = UserSummaryResponse.builder()
                .id(1L)
                .username("user")
                .email("user@example.com")
                .build();

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void equals_shouldReturnFalseForDifferentContent() {
        UserSummaryResponse response1 = UserSummaryResponse.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        UserSummaryResponse response2 = UserSummaryResponse.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .build();

        assertThat(response1).isNotEqualTo(response2);
    }

    @Test
    void toString_shouldContainAllFields() {
        UserSummaryResponse response = UserSummaryResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        String toString = response.toString();

        assertThat(toString).contains("id=1");
        assertThat(toString).contains("username=testuser");
        assertThat(toString).contains("email=test@example.com");
    }

    @Test
    void withNullValues_shouldHandleCorrectly() {
        UserSummaryResponse response = UserSummaryResponse.builder()
                .id(null)
                .username(null)
                .email(null)
                .build();

        assertThat(response.getId()).isNull();
        assertThat(response.getUsername()).isNull();
        assertThat(response.getEmail()).isNull();
    }

    @Test
    void withDifferentEmailFormats_shouldPreserveFormat() {
        String[] emails = {
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org"
        };

        for (String email : emails) {
            UserSummaryResponse response = UserSummaryResponse.builder()
                    .id(1L)
                    .username("user")
                    .email(email)
                    .build();

            assertThat(response.getEmail()).isEqualTo(email);
        }
    }
}

