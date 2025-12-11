package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionMapperTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SessionMapperImpl sessionMapper;

    private Teacher teacher;
    private User user1;
    private User user2;
    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        teacher = Teacher.builder()
            .id(1L)
            .firstName("Pierre")
            .lastName("Martin")
            .build();

        user1 = User.builder()
            .id(1L)
            .email("test@test.fr")
            .firstName("Jean")
            .lastName("Dupont")
            .password("password123")
            .admin(false)
            .build();

        user2 = User.builder()
            .id(2L)
            .email("test2@test.fr")
            .firstName("Marie")
            .lastName("Durand")
            .password("password321")
            .admin(false)
            .build();

        session = Session.builder()
            .id(1L)
            .name("Yoga Session")
            .description("Session description")
            .date(new Date())
            .teacher(teacher)
            .users(Arrays.asList(user1, user2))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDescription("Session description");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Arrays.asList(1L, 2L));
    }

    // ========== Tests toEntity ==========

    @Test
    void testToEntitySuccess() {
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(user2);

        Session result = sessionMapper.toEntity(sessionDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Yoga Session");
        assertThat(result.getDescription()).isEqualTo("Session description");
        assertThat(result.getTeacher()).isEqualTo(teacher);
        assertThat(result.getUsers()).hasSize(2);
        assertThat(result.getUsers()).containsExactly(user1, user2);

        verify(teacherService).findById(1L);
        verify(userService).findById(1L);
        verify(userService).findById(2L);
    }

    @Test
    void testToEntityWithNullTeacherId() {
        sessionDto.setTeacher_id(null);
        sessionDto.setUsers(Collections.emptyList());

        Session result = sessionMapper.toEntity(sessionDto);

        assertThat(result).isNotNull();
        assertThat(result.getTeacher()).isNull();
        verify(teacherService, never()).findById(any());
    }

    @Test
    void testToEntityWithNullUsers() {
        when(teacherService.findById(1L)).thenReturn(teacher);
        sessionDto.setUsers(null);

        Session result = sessionMapper.toEntity(sessionDto);

        assertThat(result).isNotNull();
        assertThat(result.getUsers()).isEmpty();
        verify(userService, never()).findById(any());
    }

    @Test
    void testToEntityWithEmptyUsers() {
        when(teacherService.findById(1L)).thenReturn(teacher);
        sessionDto.setUsers(Collections.emptyList());

        Session result = sessionMapper.toEntity(sessionDto);

        assertThat(result).isNotNull();
        assertThat(result.getUsers()).isEmpty();
        verify(userService, never()).findById(any());
    }

    @Test
    void testToEntityWithNullUser() {
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(null);

        Session result = sessionMapper.toEntity(sessionDto);

        assertThat(result).isNotNull();
        assertThat(result.getUsers()).containsExactly(user1, null);
    }

    @Test
    void testToDtoSuccess() {
        SessionDto result = sessionMapper.toDto(session);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Yoga Session");
        assertThat(result.getDescription()).isEqualTo("Session description");
        assertThat(result.getTeacher_id()).isEqualTo(1L);
        assertThat(result.getUsers()).hasSize(2);
        assertThat(result.getUsers()).containsExactly(1L, 2L);
    }

    @Test
    void testToDtoWithNullUsers() {
        session.setUsers(null);

        SessionDto result = sessionMapper.toDto(session);

        assertThat(result).isNotNull();
        assertThat(result.getUsers()).isEmpty();
    }

    @Test
    void testToDtoWithEmptyUsers() {
        session.setUsers(Collections.emptyList());

        SessionDto result = sessionMapper.toDto(session);

        assertThat(result).isNotNull();
        assertThat(result.getUsers()).isEmpty();
    }

    @Test
    void testToEntityList() {
        SessionDto dto1 = new SessionDto();
        dto1.setId(1L);
        dto1.setName("Session 1");
        dto1.setDescription("Description 1");
        dto1.setTeacher_id(1L);
        dto1.setUsers(Arrays.asList(1L));

        SessionDto dto2 = new SessionDto();
        dto2.setId(2L);
        dto2.setName("Session 2");
        dto2.setDescription("Description 2");
        dto2.setTeacher_id(1L);
        dto2.setUsers(Arrays.asList(2L));

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(user2);

        List<Session> result = sessionMapper.toEntity(Arrays.asList(dto1, dto2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Session 1");
        assertThat(result.get(1).getName()).isEqualTo("Session 2");
    }

    @Test
    void testToDtoList() {
        Session session1 = Session.builder()
            .id(1L)
            .name("Session 1")
            .description("Description 1")
            .teacher(teacher)
            .users(Arrays.asList(user1))
            .build();

        Session session2 = Session.builder()
            .id(2L)
            .name("Session 2")
            .description("Description 2")
            .teacher(teacher)
            .users(Arrays.asList(user2))
            .build();

        List<SessionDto> result = sessionMapper.toDto(Arrays.asList(session1, session2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Session 1");
        assertThat(result.get(1).getName()).isEqualTo("Session 2");
        assertThat(result.get(0).getUsers()).containsExactly(1L);
        assertThat(result.get(1).getUsers()).containsExactly(2L);
    }
}