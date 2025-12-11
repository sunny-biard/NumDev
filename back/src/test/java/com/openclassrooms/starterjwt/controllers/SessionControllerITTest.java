package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class SessionControllerITTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private SessionRepository sessionRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private TeacherRepository teacherRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        private Session session;

        private User testUser;

        private Teacher testTeacher;

        private String token;

        @BeforeEach
        void setup() throws Exception {
                SecurityContextHolder.clearContext();

                sessionRepository.deleteAll();
                userRepository.deleteAll();
                teacherRepository.deleteAll();

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

                session = Session.builder()
                        .name("Yoga Friday")
                        .description("Friday session")
                        .date(new java.util.Date())
                        .teacher(testTeacher)
                        .users(new ArrayList<>())
                        .build();

                session = sessionRepository.save(session);
        }

        @AfterEach
        void cleanup() {
                SecurityContextHolder.clearContext();
        }

        @Test
        void testFindById() throws Exception {
        mockMvc.perform(get("/api/session/" + session.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Yoga Friday"));
        }

        @Test
        void testFindByIdInvalidId() throws Exception {
        mockMvc.perform(get("/api/session/invalid")
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
        }

        @Test
        void testFindAllSessions() throws Exception {
        mockMvc.perform(get("/api/session")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        }

        @Test
        void testCreateSession() throws Exception {
                String json = "{"
                        + "\"name\":\"Yoga Débutant\","
                        + "\"date\":\"2025-12-10\","
                        + "\"teacher_id\":" + testTeacher.getId() + ","
                        + "\"description\":\"Session de yoga pour débutants\""
                        + "}";

                mockMvc.perform(post("/api/session")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", token)
                                .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("Yoga Débutant"))
                        .andExpect(jsonPath("$.description").value("Session de yoga pour débutants"))
                        .andExpect(jsonPath("$.teacher_id").value(testTeacher.getId()));
        }

        @Test
        void testCreateSessionInvalidForm() throws Exception {
                String json = "{"
                        + "\"date\":\"2025-12-10\","
                        + "\"teacher_id\":" + testTeacher.getId() + ","
                        + "\"description\":\"Sans nom\""
                        + "}";

                mockMvc.perform(post("/api/session")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", token)
                                .content(json))
                        .andExpect(status().isBadRequest());
        }

        @Test
        void testUpdateSession() throws Exception {
                String json = "{"
                        + "\"name\":\"Yoga Updated\","
                        + "\"date\":\"2025-12-10\","
                        + "\"teacher_id\":" + testTeacher.getId() + ","
                        + "\"description\":\"Description mise à jour\""
                        + "}";

                mockMvc.perform(put("/api/session/" + session.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", token)
                                .content(json))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("Yoga Updated"));
        }

        @Test
        void testUpdateSessionInvalidId() throws Exception {
                String json = "{"
                        + "\"name\":\"Test\","
                        + "\"date\":\"2025-12-10\","
                        + "\"teacher_id\":" + testTeacher.getId() + ","
                        + "\"description\":\"Test\""
                        + "}";

                mockMvc.perform(put("/api/session/invalid")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", token)
                                .content(json))
                        .andExpect(status().isBadRequest());
        }

        @Test
        void testDeleteSession() throws Exception {
                mockMvc.perform(delete("/api/session/" + session.getId())
                                .header("Authorization", token))
                        .andExpect(status().isOk());
        }

        @Test
        void testDeleteSessionNotFound() throws Exception {
                mockMvc.perform(delete("/api/session/9999")
                                .header("Authorization", token))
                        .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteSessionInvalidId() throws Exception {
                mockMvc.perform(delete("/api/session/invalid")
                                .header("Authorization", token))
                        .andExpect(status().isBadRequest());
        }

        @Test
        void testParticipateSession() throws Exception {
                mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + testUser.getId())
                                .header("Authorization", token))
                        .andExpect(status().isOk());
        }

        @Test
        void testNoLongerParticipateSession() throws Exception {
                mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + testUser.getId())
                                .header("Authorization", token))
                        .andExpect(status().isOk());
                
                mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + testUser.getId())
                                .header("Authorization", token))
                        .andExpect(status().isOk());
        }

        @Test
        void testParticipateInvalidIds() throws Exception {
                mockMvc.perform(post("/api/session/abc/participate/def")
                                .header("Authorization", token))
                        .andExpect(status().isBadRequest());
        }

        @Test
        void testNoLongerParticipateInvalidIds() throws Exception {
                mockMvc.perform(delete("/api/session/abc/participate/def")
                                .header("Authorization", token))
                        .andExpect(status().isBadRequest());
        }

        @Test
        void testFindNonExistingSession() throws Exception {
                mockMvc.perform(get("/api/session/9999")
                                .header("Authorization", token))
                        .andExpect(status().isNotFound());
        }
}
