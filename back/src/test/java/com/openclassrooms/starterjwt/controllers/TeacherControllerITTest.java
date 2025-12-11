package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class TeacherControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    private Teacher testTeacher;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        SecurityContextHolder.clearContext();

        teacherRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .email("test@test.fr")
                .firstName("Jean")
                .lastName("Dupont")
                .password(passwordEncoder.encode("password123"))
                .admin(false)
                .build();

        userRepository.save(testUser);

        testTeacher = Teacher.builder()
            .firstName("Pierre")
            .lastName("Martin")
            .build();
                        
        teacherRepository.save(testTeacher);

        token = TokenGenerator.getAuthToken(mockMvc, "test@test.fr", "password123");
    }

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testFindById() throws Exception {
        mockMvc.perform(get("/api/teacher/" + testTeacher.getId())
                .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testTeacher.getId()))
        .andExpect(jsonPath("$.firstName").value("Pierre"))
        .andExpect(jsonPath("$.lastName").value("Martin"));
    }

    @Test
    void testFindByIdInvalidId() throws Exception {
        mockMvc.perform(get("/api/teacher/invalid")
                .header("Authorization", token))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testFindByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/teacher/9999")
                .header("Authorization", token))
            .andExpect(status().isNotFound());
    }

    @Test
    void testFindAllTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher")
                .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(testTeacher.getId()))
            .andExpect(jsonPath("$[0].firstName").value("Pierre"))
            .andExpect(jsonPath("$[0].lastName").value("Martin"));
    }
}