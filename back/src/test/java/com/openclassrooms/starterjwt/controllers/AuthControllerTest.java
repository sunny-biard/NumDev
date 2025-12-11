package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.payload.response.MessageResponse;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    private User testUser;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.fr");
        testUser.setFirstName("Jean");
        testUser.setLastName("Dupont");
        testUser.setPassword("password123");
        testUser.setAdmin(false);

        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.fr")
                .firstName("Jean")
                .lastName("Dupont")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("Should authenticate user and return JWT token")
    void shouldAuthenticateUserAndReturnJwtToken() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.fr");
        loginRequest.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token-123");
        when(userRepository.findByEmail("test@test.fr")).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse.getToken()).isEqualTo("jwt-token-123");
        assertThat(jwtResponse.getId()).isEqualTo(1L);
        assertThat(jwtResponse.getUsername()).isEqualTo("test@test.fr");
        assertThat(jwtResponse.getFirstName()).isEqualTo("Jean");
        assertThat(jwtResponse.getLastName()).isEqualTo("Dupont");
        assertThat(jwtResponse.getAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should authenticate admin user with admin flag true")
    void shouldAuthenticatetestAdminWithAdminFlagTrue() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.fr");
        loginRequest.setPassword("password123");

        User testAdmin = new User();
        testAdmin.setId(2L);
        testAdmin.setEmail("test@test.fr");
        testAdmin.setAdmin(true);

        UserDetailsImpl adminDetails = UserDetailsImpl.builder()
                .id(2L)
                .username("test@test.fr")
                .firstName("Jean")
                .lastName("Dupont")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("admin-jwt-token");
        when(userRepository.findByEmail("test@test.fr")).thenReturn(Optional.of(testAdmin));

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse.getAdmin()).isTrue();
    }

    @Test
    @DisplayName("Should handle user not found in database during login")
    void shouldHandleUserNotFoundInDatabaseDuringLogin() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.fr");
        loginRequest.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        when(userRepository.findByEmail("test@test.fr")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertThat(jwtResponse.getAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@test.fr");
        signupRequest.setFirstName("Jean");
        signupRequest.setLastName("Dupont");
        signupRequest.setPassword("password123");

        when(userRepository.existsByEmail("test@test.fr")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(messageResponse.getMessage()).isEqualTo("User registered successfully!");
        verify(userRepository).existsByEmail("test@test.fr");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should return error when email already exists")
    void shouldReturnErrorWhenEmailAlreadyExists() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@test.fr");
        signupRequest.setFirstName("Jean");
        signupRequest.setLastName("Dupont");
        signupRequest.setPassword("password123");

        when(userRepository.existsByEmail("test@test.fr")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertThat(messageResponse.getMessage()).isEqualTo("Error: Email is already taken!");
        verify(userRepository).existsByEmail("test@test.fr");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }
}