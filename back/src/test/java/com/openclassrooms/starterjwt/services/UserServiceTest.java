package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
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
@DisplayName("UserService Tests")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.fr");
        testUser.setLastName("Dupont");
        testUser.setFirstName("Jean");
        testUser.setPassword("password123");
        testUser.setAdmin(false);
    }

    @Test
    @DisplayName("Should find user by id when user exists")
    void shouldFindUserByIdWhenUserExists() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        User result = userService.findById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo("test@test.fr");
        assertThat(result.getLastName()).isEqualTo("Dupont");
        assertThat(result.getFirstName()).isEqualTo("Jean");
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should return null when trying to find user with invalid id")
    void shouldReturnNullWhenUserDoesNotExist() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User result = userService.findById(userId);

        assertThat(result).isNull();
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should delete user when valid id is provided")
    void shouldDeleteUserWhenValidIdProvided() {
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);
        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}

