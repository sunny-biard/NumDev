package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("UserService Integration Tests")
public class UserServiceITTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.fr")
                .firstName("Jean")
                .lastName("Dupont")
                .password("password123")
                .admin(false)
                .build();
        testUser = userRepository.save(testUser);

        testUser2 = User.builder()
                .email("test2@test.fr")
                .firstName("Marie")
                .lastName("Durand")
                .password("password456")
                .admin(true)
                .build();
        testUser2 = userRepository.save(testUser2);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        User foundUser = userService.findById(testUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(testUser.getId());
        assertThat(foundUser.getEmail()).isEqualTo("test@test.fr");
        assertThat(foundUser.getFirstName()).isEqualTo("Jean");
        assertThat(foundUser.getLastName()).isEqualTo("Dupont");
        assertThat(foundUser.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("Should return null when user does not exist")
    void shouldReturnNullWhenUserNotFound() {
        User foundUser = userService.findById(999L);

        assertThat(foundUser).isNull();
    }

    @Test
    @DisplayName("Should find admin user correctly")
    void shouldFindAdminUserCorrectly() {
        User foundUser = userService.findById(testUser2.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(testUser2.getId());
        assertThat(foundUser.getEmail()).isEqualTo("test2@test.fr");
        assertThat(foundUser.isAdmin()).isTrue();
    }

    @Test
    @DisplayName("Should delete user by ID")
    void shouldDeleteUserById() {
        Long userId = testUser.getId();

        userService.delete(userId);

        User deletedUser = userRepository.findById(userId).orElse(null);
        assertThat(deletedUser).isNull();
    }
}