package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherController Tests")
public class TeacherControllerTest {

    @InjectMocks
    private TeacherController teacherController;

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    private Teacher testTeacher;
    private Teacher testTeacher2;

    private TeacherDto teacherDto;
    private TeacherDto teacherDto2;

    @BeforeEach
    public void setUp() {
        testTeacher = new Teacher();
        testTeacher.setId(1L);
        testTeacher.setLastName("Dupont");
        testTeacher.setFirstName("Jean");

        testTeacher2 = new Teacher();
        testTeacher2.setId(2L);
        testTeacher2.setLastName("Martin");
        testTeacher2.setFirstName("Claire");

        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setLastName("Dupont");
        teacherDto.setFirstName("Jean");

        teacherDto2 = new TeacherDto();
        teacherDto2.setId(2L);
        teacherDto2.setLastName("Martin");
        teacherDto2.setFirstName("Claire");
    }

    @Test
    @DisplayName("Should get teacher by ID successfully")
    public void testGetTeacherById() {
        Long teacherId = 1L;

        when(teacherService.findById(teacherId)).thenReturn(testTeacher);
        when(teacherMapper.toDto(testTeacher)).thenReturn(teacherDto);

        ResponseEntity<?> response = teacherController.findById(teacherId.toString());

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        assertThat(response.getBody()).isEqualTo(teacherDto);
    }

    @Test
    @DisplayName("Should return 404 when teacher not found by ID")
    public void testGetTeacherByIdNotFound() {
        Long teacherId = 99L;

        when(teacherService.findById(teacherId)).thenReturn(null);

        ResponseEntity<?> response = teacherController.findById(teacherId.toString());

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should return 400 for invalid teacher ID")
    public void testGetTeacherByIdInvalid() {
        ResponseEntity<?> response = teacherController.findById("invalid");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should get all teachers successfully")
    public void testGetAllTeachers() {
        List<Teacher> teachers = List.of(testTeacher, testTeacher2);
        List<TeacherDto> teacherDtos = List.of(teacherDto, teacherDto2);

        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

        ResponseEntity<?> response = teacherController.findAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        assertThat(response.getBody()).isEqualTo(teacherDtos);
    }
}
