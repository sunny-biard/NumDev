package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("UserController Integration Tests")
public class UserControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
        
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        SecurityContextHolder.clearContext();

        userRepository.deleteAll();

        testUser = User.builder()
                .email("test@test.fr")
                .firstName("Jean")
                .lastName("Dupont")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build();

        userRepository.save(testUser);

        token = TokenGenerator.getAuthToken(mockMvc, "test@test.fr", "password123");
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testFindById() throws Exception {
        mockMvc.perform(get("/api/user/" + testUser.getId())
                .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testUser.getId()))
        .andExpect(jsonPath("$.email").value("test@test.fr"));
    }

    @Test
    void testFindByIdInvalidId() throws Exception {
        mockMvc.perform(get("/api/user/invalid")
                .header("Authorization", token))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testFindByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/user/9999")
                .header("Authorization", token))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/user/" + testUser.getId())
                    .header("Authorization", token))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteUserNotFound() throws Exception {
         mockMvc.perform(delete("/api/user/9999")
                    .header("Authorization", token))
            .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUserInvalidId() throws Exception {
        mockMvc.perform(delete("/api/user/invalid")
                    .header("Authorization", token))
            .andExpect(status().isBadRequest());
    }
}
