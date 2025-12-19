package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("TeacherService Integration Tests")
public class TeacherServiceITTest {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher testTeacher;
    private Teacher testTeacher2;

    @BeforeEach
    void setUp() {
        testTeacher = Teacher.builder()
                .firstName("Pierre")
                .lastName("Martin")
                .build();
        testTeacher = teacherRepository.save(testTeacher);

        testTeacher2 = Teacher.builder()
                .firstName("Marie")
                .lastName("Durand")
                .build();
        testTeacher2 = teacherRepository.save(testTeacher2);
    }

    @AfterEach
    void tearDown() {
        teacherRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find all teachers")
    void shouldFindAllTeachers() {
        List<Teacher> teachers = teacherService.findAll();

        assertThat(teachers).isNotNull();
        assertThat(teachers).hasSize(2);
        assertThat(teachers).extracting(Teacher::getFirstName)
                .containsExactlyInAnyOrder("Pierre", "Marie");
        assertThat(teachers).extracting(Teacher::getLastName)
                .containsExactlyInAnyOrder("Martin", "Durand");
    }

    @Test
    @DisplayName("Should return empty list when no teachers exist")
    void shouldReturnEmptyListWhenNoTeachers() {
        teacherRepository.deleteAll();

        List<Teacher> teachers = teacherService.findAll();

        assertThat(teachers).isNotNull();
        assertThat(teachers).isEmpty();
    }

    @Test
    @DisplayName("Should find teacher by ID")
    void shouldFindTeacherById() {
        Teacher foundTeacher = teacherService.findById(testTeacher.getId());

        assertThat(foundTeacher).isNotNull();
        assertThat(foundTeacher.getId()).isEqualTo(testTeacher.getId());
        assertThat(foundTeacher.getFirstName()).isEqualTo("Pierre");
        assertThat(foundTeacher.getLastName()).isEqualTo("Martin");
    }

    @Test
    @DisplayName("Should return null when teacher does not exist")
    void shouldReturnNullWhenTeacherNotFound() {
        Teacher foundTeacher = teacherService.findById(999L);

        assertThat(foundTeacher).isNull();
    }

    @Test
    @DisplayName("Should find correct teacher among multiple teachers")
    void shouldFindCorrectTeacherAmongMultiple() {
        Teacher foundTeacher = teacherService.findById(testTeacher2.getId());

        assertThat(foundTeacher).isNotNull();
        assertThat(foundTeacher.getId()).isEqualTo(testTeacher2.getId());
        assertThat(foundTeacher.getFirstName()).isEqualTo("Marie");
        assertThat(foundTeacher.getLastName()).isEqualTo("Durand");
    }
}