package com.openclassrooms.starterjwt.payload;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.payload.response.JwtResponse;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtResponse Tests")
class JwtResponseTest {

    @Test
    @DisplayName("Should create JwtResponse with all parameters")
    void shouldCreateJwtResponseWithAllParameters() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        Long id = 1L;
        String username = "test@test.fr";
        String firstName = "Jean";
        String lastName = "Dupont";
        Boolean admin = false;

        JwtResponse response = new JwtResponse(token, id, username, firstName, lastName, admin);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(token);
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getUsername()).isEqualTo(username);
        assertThat(response.getFirstName()).isEqualTo(firstName);
        assertThat(response.getLastName()).isEqualTo(lastName);
        assertThat(response.getAdmin()).isFalse();
    }
}
