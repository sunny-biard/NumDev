package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    void testToEntity() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("test@test.fr");
        dto.setFirstName("Jean");
        dto.setLastName("Dupont");
        dto.setPassword("password123");
        dto.setAdmin(false);

        User result = userMapper.toEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.fr");
        assertThat(result.getFirstName()).isEqualTo("Jean");
        assertThat(result.getLastName()).isEqualTo("Dupont");
        assertThat(result.getPassword()).isEqualTo("password123");
        assertThat(result.isAdmin()).isFalse();
    }

    @Test
    void testToDto() {
        User user = User.builder()
            .id(1L)
            .email("test@test.fr")
            .firstName("Jean")
            .lastName("Dupont")
            .password("password123")
            .admin(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        UserDto result = userMapper.toDto(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.fr");
        assertThat(result.getFirstName()).isEqualTo("Jean");
        assertThat(result.getLastName()).isEqualTo("Dupont");
        assertThat(result.getPassword()).isEqualTo("password123");
        assertThat(result.isAdmin()).isFalse();
    }

    @Test
    void testToEntityList() {
        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setEmail("user1@test.fr");
        dto1.setFirstName("Jean");
        dto1.setLastName("Dupont");
        dto1.setPassword("password123");
        dto1.setAdmin(false);

        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setEmail("user2@test.fr");
        dto2.setFirstName("Marie");
        dto2.setLastName("Durand");
        dto2.setPassword("password456");
        dto2.setAdmin(true);

        List<User> result = userMapper.toEntity(Arrays.asList(dto1, dto2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("user1@test.fr");
        assertThat(result.get(1).getEmail()).isEqualTo("user2@test.fr");
    }

    @Test
    void testToDtoList() {
        User user1 = User.builder()
            .id(1L)
            .email("user1@test.fr")
            .firstName("Jean")
            .lastName("Dupont")
            .password("password123")
            .admin(false)
            .build();

        User user2 = User.builder()
            .id(2L)
            .email("user2@test.fr")
            .firstName("Marie")
            .lastName("Durand")
            .password("password456")
            .admin(true)
            .build();

        List<UserDto> result = userMapper.toDto(Arrays.asList(user1, user2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo("user1@test.fr");
        assertThat(result.get(1).getEmail()).isEqualTo("user2@test.fr");
    }
}