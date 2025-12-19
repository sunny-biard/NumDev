package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("SessionService Integration Tests")
public class SessionServiceITTest {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Session testSession;
    private User testUser;
    private User testUser2;
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        testTeacher = Teacher.builder()
                .firstName("Pierre")
                .lastName("Martin")
                .build();
        testTeacher = teacherRepository.save(testTeacher);

        testUser = User.builder()
                .email("test@test.fr")
                .firstName("Jean")
                .lastName("Dupont")
                .password("password123")
                .admin(false)
                .build();
        testUser = userRepository.save(testUser);

        testUser2 = User.builder()
                .email("test2@test.fr")
                .firstName("Marie")
                .lastName("Durand")
                .password("password456")
                .admin(false)
                .build();
        testUser2 = userRepository.save(testUser2);

        testSession = Session.builder()
                .name("Yoga Friday")
                .description("Friday session")
                .date(new Date())
                .teacher(testTeacher)
                .users(new ArrayList<>())
                .build();
        testSession = sessionRepository.save(testSession);
    }

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create a new session")
    void shouldCreateSession() {
        Session newSession = Session.builder()
                .name("Yoga Saturday")
                .description("Saturday session")
                .date(new Date())
                .teacher(testTeacher)
                .users(new ArrayList<>())
                .build();

        Session savedSession = sessionService.create(newSession);

        assertThat(savedSession).isNotNull();
        assertThat(savedSession.getId()).isNotNull();
        assertThat(savedSession.getName()).isEqualTo("Yoga Saturday");
        assertThat(savedSession.getDescription()).isEqualTo("Saturday session");
        assertThat(savedSession.getTeacher().getId()).isEqualTo(testTeacher.getId());
    }

    @Test
    @DisplayName("Should find all sessions")
    void shouldFindAllSessions() {
        Session session2 = Session.builder()
                .name("Yoga Sunday")
                .description("Sunday session")
                .date(new Date())
                .teacher(testTeacher)
                .users(new ArrayList<>())
                .build();
        sessionRepository.save(session2);

        List<Session> sessions = sessionService.findAll();

        assertThat(sessions).isNotNull();
        assertThat(sessions).hasSize(2);
        assertThat(sessions).extracting(Session::getName)
                .containsExactlyInAnyOrder("Yoga Friday", "Yoga Sunday");
    }

    @Test
    @DisplayName("Should get session by ID")
    void shouldGetSessionById() {
        Session foundSession = sessionService.getById(testSession.getId());

        assertThat(foundSession).isNotNull();
        assertThat(foundSession.getId()).isEqualTo(testSession.getId());
        assertThat(foundSession.getName()).isEqualTo("Yoga Friday");
        assertThat(foundSession.getDescription()).isEqualTo("Friday session");
    }

    @Test
    @DisplayName("Should return null when getting session by non-existing ID")
    void shouldReturnNullWhenSessionNotFound() {
        Session foundSession = sessionService.getById(999L);

        assertThat(foundSession).isNull();
    }

    @Test
    @DisplayName("Should update an existing session")
    void shouldUpdateSession() {
        Session updatedSession = Session.builder()
                .name("Yoga Friday Updated")
                .description("Updated description")
                .date(new Date())
                .teacher(testTeacher)
                .users(new ArrayList<>())
                .build();

        Session result = sessionService.update(testSession.getId(), updatedSession);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testSession.getId());
        assertThat(result.getName()).isEqualTo("Yoga Friday Updated");
        assertThat(result.getDescription()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("Should delete a session")
    void shouldDeleteSession() {
        Long sessionId = testSession.getId();

        sessionService.delete(sessionId);

        Session deletedSession = sessionRepository.findById(sessionId).orElse(null);
        assertThat(deletedSession).isNull();
    }

    @Test
    @DisplayName("Should add user to session participants")
    void shouldParticipateUserInSession() {
        sessionService.participate(testSession.getId(), testUser.getId());

        Session updatedSession = sessionRepository.findById(testSession.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getUsers()).hasSize(1);
        assertThat(updatedSession.getUsers().get(0).getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should add multiple users to session")
    void shouldParticipateMultipleUsers() {
        sessionService.participate(testSession.getId(), testUser.getId());
        sessionService.participate(testSession.getId(), testUser2.getId());

        Session updatedSession = sessionRepository.findById(testSession.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getUsers()).hasSize(2);
        assertThat(updatedSession.getUsers())
                .extracting(User::getId)
                .containsExactlyInAnyOrder(testUser.getId(), testUser2.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException when session does not exist for participation")
    void shouldThrowNotFoundExceptionWhenSessionNotExistsForParticipation() {
        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(999L, testUser.getId());
        });
    }

    @Test
    @DisplayName("Should throw NotFoundException when user does not exist for participation")
    void shouldThrowNotFoundExceptionWhenUserNotExistsForParticipation() {
        assertThrows(NotFoundException.class, () -> {
            sessionService.participate(testSession.getId(), 999L);
        });
    }

    @Test
    @DisplayName("Should throw BadRequestException when user already participates")
    void shouldThrowBadRequestExceptionWhenUserAlreadyParticipates() {
        sessionService.participate(testSession.getId(), testUser.getId());

        assertThrows(BadRequestException.class, () -> {
            sessionService.participate(testSession.getId(), testUser.getId());
        });
    }

    @Test
    @DisplayName("Should remove user from session participants")
    void shouldNoLongerParticipateUserInSession() {
        sessionService.participate(testSession.getId(), testUser.getId());

        sessionService.noLongerParticipate(testSession.getId(), testUser.getId());

        Session updatedSession = sessionRepository.findById(testSession.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("Should remove only specified user from session")
    void shouldRemoveOnlySpecifiedUser() {
        sessionService.participate(testSession.getId(), testUser.getId());
        sessionService.participate(testSession.getId(), testUser2.getId());

        sessionService.noLongerParticipate(testSession.getId(), testUser.getId());

        Session updatedSession = sessionRepository.findById(testSession.getId()).orElse(null);
        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession.getUsers()).hasSize(1);
        assertThat(updatedSession.getUsers().get(0).getId()).isEqualTo(testUser2.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException when session does not exist for no longer participate")
    void shouldThrowNotFoundExceptionWhenSessionNotExistsForNoLongerParticipate() {
        assertThrows(NotFoundException.class, () -> {
            sessionService.noLongerParticipate(999L, testUser.getId());
        });
    }

    @Test
    @DisplayName("Should throw BadRequestException when user does not participate")
    void shouldThrowBadRequestExceptionWhenUserDoesNotParticipate() {
        assertThrows(BadRequestException.class, () -> {
            sessionService.noLongerParticipate(testSession.getId(), testUser.getId());
        });
    }
}