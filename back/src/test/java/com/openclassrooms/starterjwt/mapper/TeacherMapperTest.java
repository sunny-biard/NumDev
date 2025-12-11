package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TeacherMapperTest {

    @InjectMocks
    private TeacherMapperImpl teacherMapper;

    @Test
    void testToEntity() {
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setFirstName("Pierre");
        dto.setLastName("Martin");

        Teacher result = teacherMapper.toEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Pierre");
        assertThat(result.getLastName()).isEqualTo("Martin");
    }

    @Test
    void testToDto() {
        Teacher teacher = Teacher.builder()
            .id(1L)
            .firstName("Pierre")
            .lastName("Martin")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        TeacherDto result = teacherMapper.toDto(teacher);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Pierre");
        assertThat(result.getLastName()).isEqualTo("Martin");
    }

    @Test
    void testToEntityList() {
        TeacherDto dto1 = new TeacherDto();
        dto1.setId(1L);
        dto1.setFirstName("Pierre");
        dto1.setLastName("Martin");

        TeacherDto dto2 = new TeacherDto();
        dto2.setId(2L);
        dto2.setFirstName("Marie");
        dto2.setLastName("Durand");

        List<Teacher> result = teacherMapper.toEntity(Arrays.asList(dto1, dto2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Pierre");
        assertThat(result.get(1).getFirstName()).isEqualTo("Marie");
    }

    @Test
    void testToDtoList() {
        Teacher teacher1 = Teacher.builder()
            .id(1L)
            .firstName("Pierre")
            .lastName("Martin")
            .build();

        Teacher teacher2 = Teacher.builder()
            .id(2L)
            .firstName("Marie")
            .lastName("Durand")
            .build();

        List<TeacherDto> result = teacherMapper.toDto(Arrays.asList(teacher1, teacher2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Pierre");
        assertThat(result.get(1).getFirstName()).isEqualTo("Marie");
    }
}