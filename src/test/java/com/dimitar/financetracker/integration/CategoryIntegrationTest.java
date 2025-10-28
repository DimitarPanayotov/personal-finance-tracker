package com.dimitar.financetracker.integration;

import com.dimitar.financetracker.dto.request.category.CreateCategoryRequest;
import com.dimitar.financetracker.dto.request.category.UpdateCategoryRequest;
import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.response.user.AuthenticationResponse;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        UserRegistrationRequest registerRequest = new UserRegistrationRequest(
                "test_user",
                "test@example.com",
                "TestPass123!"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AuthenticationResponse authResponse = objectMapper.readValue(responseBody, AuthenticationResponse.class);
        jwtToken = authResponse.getToken();
    }

    @Test
    @DisplayName("Should create a new category successfully")
    void testCreateCategory() throws Exception {
        CreateCategoryRequest categoryRequest = new CreateCategoryRequest(
                "Groceries",
                CategoryType.EXPENSE,
                "#FF5733"
        );

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Groceries"))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.color").value("#FF5733"))
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(categoryRepository.findAll()).hasSize(1);
        assertThat(categoryRepository.findAll().getFirst().getName()).isEqualTo("Groceries");
    }

    @Test
    @DisplayName("Should retrieve all user categories")
    void testGetAllUserCategories() throws Exception {
        createCategory("Food", CategoryType.EXPENSE, "#FF0000");
        createCategory("Transport", CategoryType.EXPENSE, "#00FF00");
        createCategory("Salary", CategoryType.INCOME, "#0000FF");

        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[1].name").exists())
                .andExpect(jsonPath("$[2].name").exists());
    }

    @Test
    @DisplayName("Should update a category successfully")
    void testUpdateCategory() throws Exception {
        MvcResult createResult = createCategory("Old Name", CategoryType.EXPENSE, "#AAAAAA");
        String createResponse = createResult.getResponse().getContentAsString();
        Long categoryId = objectMapper.readTree(createResponse).get("id").asLong();

        UpdateCategoryRequest updateRequest = UpdateCategoryRequest.builder()
                .name("Updated Name")
                .type(CategoryType.EXPENSE)
                .color("#BBBBBB")
                .build();

        mockMvc.perform(patch("/api/categories/" + categoryId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.color").value("#BBBBBB"));

        assertThat(categoryRepository.findById(categoryId))
                .isPresent()
                .get()
                .satisfies(category -> {
                    assertThat(category.getName()).isEqualTo("Updated Name");
                    assertThat(category.getColor()).isEqualTo("#BBBBBB");
                });
    }

    @Test
    @DisplayName("Should delete a category successfully")
    void testDeleteCategory() throws Exception {
        MvcResult createResult = createCategory("To Delete", CategoryType.EXPENSE, "#FFFFFF");
        String createResponse = createResult.getResponse().getContentAsString();
        Long categoryId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/api/categories/" + categoryId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        assertThat(categoryRepository.findById(categoryId)).isEmpty();
        assertThat(categoryRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should prevent access without authentication token")
    void testUnauthorizedAccess() throws Exception {
        CreateCategoryRequest categoryRequest = new CreateCategoryRequest(
                "Unauthorized",
                CategoryType.EXPENSE,
                "#000000"
        );

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should prevent users from accessing other users' categories")
    void testCrossTenantAccess() throws Exception {
        MvcResult createResult = createCategory("User1 Category", CategoryType.EXPENSE, "#123456");
        String createResponse = createResult.getResponse().getContentAsString();
        long categoryId = objectMapper.readTree(createResponse).get("id").asLong();

        UserRegistrationRequest secondUserRequest = new UserRegistrationRequest(
                "second_user",
                "second@example.com",
                "SecondPass123!"
        );

        MvcResult secondUserResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondUserRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String secondUserResponse = secondUserResult.getResponse().getContentAsString();
        AuthenticationResponse secondAuthResponse = objectMapper.readValue(secondUserResponse, AuthenticationResponse.class);
        String secondUserToken = secondAuthResponse.getToken();

        mockMvc.perform(get("/api/categories/" + categoryId)
                        .header("Authorization", "Bearer " + secondUserToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should reject category creation with invalid data")
    void testCreateCategoryWithInvalidData() throws Exception {
        CreateCategoryRequest invalidRequest = new CreateCategoryRequest(
                "",
                CategoryType.EXPENSE,
                "#FFFFFF"
        );

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    private MvcResult createCategory(String name, CategoryType type, String color) throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest(name, type, color);

        return mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
    }
}
