package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService Tests")
public class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @InjectMocks
    private UserService userService;

    private Session testSession;
    private Session testSession2;
    private User testUser;

    @BeforeEach
    void setUp() {
        testSession = new Session();
        testSession.setId(1L);
        testSession.setName("Yoga Friday");
        testSession.setDate(new java.util.Date());
        testSession.setDescription("Friday session");
        testSession.setUsers(new ArrayList<>());

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
    }

    @Test
    @DisplayName("Should find session by id when session exists")
    void shouldFindSessionByIdWhenSessionExists() {
        Long sessionId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        Session result = sessionService.getById(sessionId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sessionId);
        assertThat(result.getName()).isEqualTo("Yoga Friday");
        assertThat(result.getDate()).isEqualTo(testSession.getDate());
        assertThat(result.getDescription()).isEqualTo("Friday session");
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    @DisplayName("Should find all sessions")
    void shouldFindAllSessions() {
        when(sessionRepository.findAll()).thenReturn(java.util.List.of(testSession, testSession2));

        java.util.List<Session> result = sessionService.findAll();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);

        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Yoga Friday");
        assertThat(result.get(0).getDescription()).isEqualTo("Friday session");
        assertThat(result.get(0).getDate()).isEqualTo(testSession.getDate());

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("Pilates Monday");
        assertThat(result.get(1).getDescription()).isEqualTo("Monday session");
        assertThat(result.get(1).getDate()).isEqualTo(testSession2.getDate());

        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return null when trying to find session with invalid id")
    void shouldReturnNullWhenSessionDoesNotExist() {
        Long sessionId = 999L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        Session result = sessionService.getById(sessionId);

        assertThat(result).isNull();
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    @DisplayName("Should create a new session")
    void shouldCreateNewSession() {
        when(sessionRepository.save(testSession)).thenReturn(testSession);

        Session result = sessionService.create(testSession);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Yoga Friday");
        assertThat(result.getDescription()).isEqualTo("Friday session");
        assertThat(result.getDate()).isEqualTo(testSession.getDate());
        verify(sessionRepository, times(1)).save(testSession);
    }

    @Test
    @DisplayName("Should update existing session")
    void shouldUpdateExistingSession() {
        Long sessionId = 1L;
        Session updatedSession = new Session();
        updatedSession.setName("Updated Yoga Friday");
        updatedSession.setDate(new java.util.Date());
        updatedSession.setDescription("Updated Friday session");    

        when(sessionRepository.save(updatedSession)).thenReturn(updatedSession);

        Session result = sessionService.update(sessionId, updatedSession);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sessionId);
        assertThat(result.getName()).isEqualTo("Updated Yoga Friday");
        assertThat(result.getDescription()).isEqualTo("Updated Friday session");
        assertThat(result.getDate()).isEqualTo(updatedSession.getDate());
        verify(sessionRepository, times(1)).save(updatedSession);
    }

    @Test
    @DisplayName("Should delete session when valid id is provided")
    void shouldDeleteSessionById() {
        Long sessionId = 1L;
        doNothing().when(sessionRepository).deleteById(sessionId);

        sessionService.delete(sessionId);

        verify(sessionRepository, times(1)).deleteById(sessionId);
    }

    @Test
    @DisplayName("Should add user to list of participants when user is not already participating")
    void shouldAddUserToParticipantsWhenNotAlreadyParticipating() {
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(sessionRepository.save(testSession)).thenReturn(testSession);

        sessionService.participate(sessionId, userId);

        assertThat(testSession.getUsers()).contains(testUser);
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);
        verify(sessionRepository, times(1)).save(testSession);
    }

    @Test
    @DisplayName("Should delete user from list of participants when user is already participating")
    void shouldDeleteUserFromParticipantsWhenAlreadyParticipating() {
        Long sessionId = 2L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession2));
        when(sessionRepository.save(testSession2)).thenReturn(testSession2);

        sessionService.noLongerParticipate(sessionId, userId);

        assertThat(testSession2.getUsers()).doesNotContain(testUser);
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(sessionRepository, times(1)).save(testSession2);
    }

    @Test
    @DisplayName("Should throw not found exception when user does not exist while participating")
    void shouldReturnNotFoundExceptionWhenUserDoesNotExistWhileParticipating() {
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw not found exception when session does not exist while participating")
    void shouldReturnNotFoundExceptionWhenSessionDoesNotExistWhileParticipating() {
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertThrows(NotFoundException.class, () -> sessionService.participate(sessionId, userId));
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw not found exception when session does not exist while unparticipating")
    void shouldReturnNotFoundExceptionWhenSessionDoesNotExistWhileUnparticipating() {
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(sessionId, userId));
        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    @DisplayName("Should throw bad request exception when user is already participating")
    void shouldReturnBadRequestExceptionWhenUserIsAlreadyParticipating() {
        Long sessionId = 2L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession2));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        
        assertThrows(BadRequestException.class, () -> sessionService.participate(sessionId, userId));
        verify(sessionRepository, times(1)).findById(sessionId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw bad request exception when user is already not participating")
    void shouldReturnBadRequestExceptionWhenUserIsAlreadyNotParticipating() {
        Long sessionId = 1L;
        Long userId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));
        
        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(sessionId, userId));
        verify(sessionRepository, times(1)).findById(sessionId);
    }
}