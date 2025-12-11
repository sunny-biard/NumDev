package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

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
    @DisplayName("Should load user by username when user exists")
    void shouldLoadUserByUsernameWhenUserExists() {
        String email = "test@test.fr";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        UserDetails result = userDetailsService.loadUserByUsername(email);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(UserDetailsImpl.class);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) result;
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("test@test.fr");
        assertThat(userDetails.getLastName()).isEqualTo("Dupont");
        assertThat(userDetails.getFirstName()).isEqualTo("Jean");
        assertThat(userDetails.getPassword()).isEqualTo("password123");
        
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        String email = "test@fail.fr";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
        
        verify(userRepository, times(1)).findByEmail(email);
    }
}

