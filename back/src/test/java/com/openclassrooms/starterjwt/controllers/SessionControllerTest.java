package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.SessionService;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionController Tests")
public class SessionControllerTest {

    @InjectMocks
    private SessionController sessionController;

    @Mock
    private SessionService sessionService;

    @Mock
    private SessionMapper sessionMapper;

    private Session testSession;
    private Session testSession2;

    private User testUser;

    private SessionDto sessionDto;
    private SessionDto sessionDto2;

    @BeforeEach
    public void setUp() {
        testSession = new Session();
        testSession.setId(1L);
        testSession.setName("Yoga Friday");
        testSession.setDate(new java.util.Date());
        testSession.setDescription("Friday session");
        testSession.setUsers(new ArrayList<>());

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Friday");
        sessionDto.setDate(new java.util.Date());
        sessionDto.setDescription("Friday session");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.fr");
        testUser.setLastName("Dupont");
        testUser.setFirstName("Jean");
        testUser.setPassword("password123");
        testUser.setAdmin(false);

        List<User> users = new ArrayList<>();
        users.add(testUser);

        testSession2 = new Session();
        testSession2.setId(2L);
        testSession2.setName("Pilates Monday");
        testSession2.setDate(new java.util.Date());
        testSession2.setDescription("Monday session");
        testSession2.setUsers(users);

        sessionDto2 = new SessionDto();
        sessionDto2.setId(2L);
        sessionDto2.setName("Pilates Monday");
        sessionDto2.setDate(new java.util.Date());
        sessionDto2.setDescription("Monday session");
    }

    @Test
    @DisplayName("Should get session by ID successfully")
    public void testGetSessionById() {
        Long sessionId = 1L;

        when(sessionService.getById(sessionId)).thenReturn(testSession);
        when(sessionMapper.toDto(testSession)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.findById(String.valueOf(sessionId));

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(sessionService).getById(sessionId);
        verify(sessionMapper).toDto(testSession);
    }

    @Test
    @DisplayName("Should return 404 when session not found by ID")
    public void testGetSessionByIdNotFound() {
        Long sessionId = 99L;

        when(sessionService.getById(sessionId)).thenReturn(null);

        ResponseEntity<?> response = sessionController.findById(String.valueOf(sessionId));

        assertThat(response.getStatusCodeValue()).isEqualTo(404);

        verify(sessionService).getById(sessionId);
    }

        @Test
    @DisplayName("Should return 400 when session ID is invalid")
    public void testGetSessionByIdInvalid() {

        ResponseEntity<?> response = sessionController.findById("invalid");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verify(sessionService, never()).getById(any());
    }

    @Test
    @DisplayName("Should get all sessions successfully")
    public void testGetAllSessions() {
        List<Session> sessions = List.of(testSession, testSession2);
        List<SessionDto> sessionDtos = List.of(sessionDto, sessionDto2);

        when(sessionService.findAll()).thenReturn(sessions);
        when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

        ResponseEntity<?> response = sessionController.findAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(sessionService).findAll();
        verify(sessionMapper).toDto(sessions);
    }

    @Test
    @DisplayName("Should create session successfully")
    public void testCreateSession() {

        when(sessionMapper.toEntity(sessionDto)).thenReturn(testSession);
        when(sessionService.create(testSession)).thenReturn(testSession);
        when(sessionMapper.toDto(testSession)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.create(sessionDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(sessionMapper).toEntity(sessionDto);
        verify(sessionService).create(testSession);
        verify(sessionMapper).toDto(testSession);
    }

    @Test
    @DisplayName("Should update session successfully")
    public void testUpdateSession() {
        Long sessionId = 1L;

        when(sessionMapper.toEntity(sessionDto)).thenReturn(testSession);
        when(sessionService.update(sessionId, testSession)).thenReturn(testSession);
        when(sessionMapper.toDto(testSession)).thenReturn(sessionDto);

        ResponseEntity<?> response = sessionController.update(String.valueOf(sessionId), sessionDto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(sessionMapper).toEntity(sessionDto);
        verify(sessionService).update(sessionId, testSession);
        verify(sessionMapper).toDto(testSession);
    }

        @Test
    @DisplayName("Should return 404 when updating with invalid session ID")
    public void testUpdateSessionNotFound() {

        ResponseEntity<?> response = sessionController.update("invalid", new SessionDto());

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verify(sessionService, never()).getById(any());
        verify(sessionService, never()).update(any(), any(Session.class));
    }

    @Test
    @DisplayName("Should delete session successfully")
    public void testDeleteSession() {
        Long sessionId = 1L;

        when(sessionService.getById(sessionId)).thenReturn(testSession);

        ResponseEntity<?> response = sessionController.save(String.valueOf(sessionId));

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(sessionService).getById(sessionId);
        verify(sessionService).delete(sessionId);
    }

    @Test
    @DisplayName("Should return 400 when deleting with invalid session ID")
    public void testDeleteSessionNotFound() {
        ResponseEntity<?> response = sessionController.save("invalid");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verify(sessionService, never()).getById(any());
        verify(sessionService, never()).delete(any());
    }

    @Test
    @DisplayName("Should participate user in session successfully")
    public void testParticipateInSession() {
        Long sessionId = 1L;
        Long userId = 1L;

        ResponseEntity<?> response = sessionController.participate(String.valueOf(sessionId), String.valueOf(userId));

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(sessionService).participate(sessionId, userId);
    }

    @Test
    @DisplayName("Should return 400 when participating with invalid user or session ID")
    public void testParticipateSessionNotFoundUserNotFound() {
        Long sessionId = 1L;
        Long userId = 1L;

        ResponseEntity<?> response = sessionController.participate("invalid", String.valueOf(userId));

        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verify(sessionService, never()).participate(any(), any());

        ResponseEntity<?> response2 = sessionController.participate(String.valueOf(sessionId), "invalid");

        assertThat(response2.getStatusCodeValue()).isEqualTo(400);

        verify(sessionService, never()).participate(any(), any());
    }

    @Test
    @DisplayName("Should no longer participate user in session successfully")
    public void testNoLongerParticipateInSession() {
        Long sessionId = 1L;
        Long userId = 1L;

        ResponseEntity<?> response = sessionController.noLongerParticipate(String.valueOf(sessionId), String.valueOf(userId));

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        verify(sessionService).noLongerParticipate(sessionId, userId);
    }

    @Test
    @DisplayName("Should return 400 when no longer participating with invalid user or session ID")
    public void testNoLongerParticipateSessionNotFoundUserNotFound() {
        Long sessionId = 1L;
        Long userId = 1L;

        ResponseEntity<?> response = sessionController.noLongerParticipate("invalid", String.valueOf(userId));
        assertThat(response.getStatusCodeValue()).isEqualTo(400);

        verify(sessionService, never()).noLongerParticipate(any(), any());

        ResponseEntity<?> response2 = sessionController.noLongerParticipate(String.valueOf(sessionId), "invalid");

        assertThat(response2.getStatusCodeValue()).isEqualTo(400);

        verify(sessionService, never()).noLongerParticipate(any(), any());
    }
}