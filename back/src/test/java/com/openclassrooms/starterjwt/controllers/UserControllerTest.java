package com.openclassrooms.starterjwt.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @Mock
    private UserDetails userDetails;

    private User testUser;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.fr");
        testUser.setLastName("Dupont");
        testUser.setFirstName("Jean");
        testUser.setPassword("password123");
        testUser.setAdmin(false);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.fr");
        userDto.setLastName("Dupont");
        userDto.setFirstName("Jean");
        userDto.setAdmin(false);
    }

    @Test
    @DisplayName("Should find user by ID")
    public void testFindById() {
        Long userId = 1L;

        when(userService.findById(userId)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(userDto);

        ResponseEntity<?> response = userController.findById(userId.toString());

        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        assertThat(response.getBody()).isEqualTo(userDto);
    }

    @Test
    @DisplayName("Should return 404 when user not found by ID")
    public void testFindByIdNotFound() {
        Long userId = 99L;

        when(userService.findById(userId)).thenReturn(null);

        ResponseEntity<?> response = userController.findById(userId.toString());

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should return 400 for invalid user ID")
    public void testFindByIdInvalid() {
        ResponseEntity<?> response = userController.findById("invalid");

        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should delete user by ID")
    public void testDeleteById() {
        Long userId = 1L;

        when(userDetails.getUsername()).thenReturn(testUser.getEmail());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "jwt-token"));
        when(userService.findById(userId)).thenReturn(testUser);

        ResponseEntity<?> response = userController.save(userId.toString());

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should return 401 when deleting user not owned by authenticated user")
    public void testDeleteByIdUnauthorized() {
        Long userId = 1L;

        when(userDetails.getUsername()).thenReturn("unauthorized@test.fr");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, "jwt-token"));
        when(userService.findById(userId)).thenReturn(testUser);

        ResponseEntity<?> response = userController.save(userId.toString());

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existing user")
    public void testDeleteByIdNotFound() {
        Long userId = 99L;

        when(userService.findById(userId)).thenReturn(null);

        ResponseEntity<?> response = userController.save(userId.toString());

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    @DisplayName("Should return 400 for invalid user ID on delete")
    public void testDeleteByIdInvalid() {
        ResponseEntity<?> response = userController.save("invalid");
        
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }
}
