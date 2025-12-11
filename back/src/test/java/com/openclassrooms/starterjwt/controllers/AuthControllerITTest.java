package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AuthControllerITTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @BeforeEach
        void setup() {
                SecurityContextHolder.clearContext();
                
                userRepository.deleteAll();

                User testUser = User.builder()
                        .email("test@test.fr")
                        .firstName("Jean")
                        .lastName("Dupont")
                        .password(passwordEncoder.encode("password123"))
                        .admin(false)
                        .build();

                userRepository.save(testUser);
        }

        @AfterEach
        void cleanup() {
                SecurityContextHolder.clearContext();
        }

        @Test
        void testLoginSuccess() throws Exception {
                String json = "{"
                        + "\"email\":\"test@test.fr\","
                        + "\"password\":\"password123\""
                        + "}";

                mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.token").exists())
                        .andExpect(jsonPath("$.username").value("test@test.fr"))
                        .andExpect(jsonPath("$.firstName").value("Jean"))
                        .andExpect(jsonPath("$.lastName").value("Dupont"))
                        .andExpect(jsonPath("$.admin").value(false));
        }

        @Test
        void testLoginBadPassword() throws Exception {
                String json = "{"
                        + "\"email\":\"test@test.fr\","
                        + "\"password\":\"wrongPassword\""
                        + "}";

                mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isUnauthorized());
        }

        @Test
        void testRegisterSuccess() throws Exception {
                String json = "{"
                        + "\"email\":\"newuser@test.fr\","
                        + "\"firstName\":\"Alice\","
                        + "\"lastName\":\"Martin\","
                        + "\"password\":\"newpassword\""
                        + "}";

                mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.message").value("User registered successfully!"));
        }

        @Test
        void testRegisterEmailAlreadyExists() throws Exception {
                String json = "{"
                        + "\"email\":\"test@test.fr\","
                        + "\"firstName\":\"Test\","
                        + "\"lastName\":\"Test\","
                        + "\"password\":\"123456\""
                        + "}";

                mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
        }

        @Test
        void testRegisterInvalidEmail() throws Exception {
                String json = "{"
                        + "\"email\":\"not-an-email\","
                        + "\"firstName\":\"Alice\","
                        + "\"lastName\":\"Martin\","
                        + "\"password\":\"newpassword\""
                        + "}";

                mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isBadRequest());
        }

        @Test
        void testLoginNonExistingUser() throws Exception {
                String json = "{"
                        + "\"email\":\"unknown@test.fr\","
                        + "\"password\":\"password123\""
                        + "}";

                mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isUnauthorized());
        }
}
