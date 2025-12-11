package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserDetailsImpl Tests")
class UserDetailsImplTest {

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.fr")
                .firstName("Jean")
                .lastName("Dupont")
                .admin(false)
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("Should create UserDetailsImpl with builder")
    void shouldCreateUserDetailsWithBuilder() {
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getId()).isEqualTo(1L);
        assertThat(userDetails.getUsername()).isEqualTo("test@test.fr");
        assertThat(userDetails.getFirstName()).isEqualTo("Jean");
        assertThat(userDetails.getLastName()).isEqualTo("Dupont");
        assertThat(userDetails.getAdmin()).isFalse();
        assertThat(userDetails.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("Should return empty authorities collection")
    void shouldReturnEmptyAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities).isNotNull();
        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Should return true for isAccountNonExpired")
    void shouldReturnTrueForIsAccountNonExpired() {
        boolean result = userDetails.isAccountNonExpired();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return true for isAccountNonLocked")
    void shouldReturnTrueForIsAccountNonLocked() {
        boolean result = userDetails.isAccountNonLocked();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return true for isCredentialsNonExpired")
    void shouldReturnTrueForIsCredentialsNonExpired() {
        boolean result = userDetails.isCredentialsNonExpired();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return true for isEnabled")
    void shouldReturnTrueForIsEnabled() {
        boolean result = userDetails.isEnabled();

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should be equal when ids are the same")
    void shouldBeEqualWhenIdsAreSame() {
        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(1L)
                .username("test2@test.fr")
                .firstName("Alice")
                .lastName("Martin")
                .password("password321")
                .build();

        assertThat(userDetails).isEqualTo(user2);
        assertThat(userDetails.equals(user2)).isTrue();
    }

    @Test
    @DisplayName("Should not be equal when ids are different")
    void shouldNotBeEqualWhenIdsAreDifferent() {
        UserDetailsImpl user2 = UserDetailsImpl.builder()
                .id(2L)
                .username("test@test.fr")
                .firstName("John")
                .lastName("Dupont")
                .password("password123")
                .build();

        assertThat(userDetails).isNotEqualTo(user2);
        assertThat(userDetails.equals(user2)).isFalse();
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        assertThat(userDetails).isEqualTo(userDetails);
        assertThat(userDetails.equals(userDetails)).isTrue();
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        assertThat(userDetails).isNotEqualTo(null);
        assertThat(userDetails.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void shouldNotBeEqualToDifferentClass() {
        String differentObject = "Not a UserDetailsImpl";

        assertThat(userDetails).isNotEqualTo(differentObject);
        assertThat(userDetails.equals(differentObject)).isFalse();
    }
}
