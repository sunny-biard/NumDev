package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherService Tests")
public class TeacherServiceTest {
    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher testTeacher;
    private Teacher testTeacher2;

    @BeforeEach
    void setUp() {
        testTeacher = new Teacher();
        testTeacher.setId(1L);
        testTeacher.setLastName("Dupont");
        testTeacher.setFirstName("Jean");

        testTeacher2 = new Teacher();
        testTeacher2.setId(2L);
        testTeacher2.setLastName("Martin");
        testTeacher2.setFirstName("Claire");
    }

    @Test
    @DisplayName("Should find teacher by id when teacher exists")
    void shouldFindTeacherByIdWhenTeacherExists() {
        Long teacherId = 1L;
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(testTeacher));

        Teacher result = teacherService.findById(teacherId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(teacherId);
        assertThat(result.getLastName()).isEqualTo("Dupont");
        assertThat(result.getFirstName()).isEqualTo("Jean");
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    @DisplayName("Should return null when trying to find teacher with invalid id")
    void shouldReturnNullWhenTeacherDoesNotExist() {
        Long teacherId = 999L;
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

        Teacher result = teacherService.findById(teacherId);

        assertThat(result).isNull();
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    @DisplayName("Should find all teachers")
    void shouldFindAllTeachers() {
        when(teacherRepository.findAll()).thenReturn(java.util.List.of(testTeacher, testTeacher2));

        java.util.List<Teacher> result = teacherService.findAll();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);

        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getLastName()).isEqualTo("Dupont");
        assertThat(result.get(0).getFirstName()).isEqualTo("Jean");

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getLastName()).isEqualTo("Martin");
        assertThat(result.get(1).getFirstName()).isEqualTo("Claire");
        
        verify(teacherRepository, times(1)).findAll();
    }
}
