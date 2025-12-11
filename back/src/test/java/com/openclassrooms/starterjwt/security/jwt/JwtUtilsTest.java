package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("JwtUtils Tests")
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private UserDetailsImpl userDetails;
    private String testSecret = "testSecretKeyForJwtTokenGenerationAndValidation";
    private int testExpirationMs = 3600000;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", testExpirationMs);

        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@test.fr")
                .firstName("Jean")
                .lastName("Dupont")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("Should generate valid JWT token and extract username")
    void shouldGenerateValidJwtTokenAndExtractUsername() {
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertThat(token).isNotNull();
        assertThat(username).isEqualTo("test@test.fr");
    }

    @Test
    @DisplayName("Should validate correct JWT token")
    void shouldValidateCorrectJwtToken() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should return false for invalid signature")
    void shouldReturnFalseForInvalidSignature() {
        String tokenWithBadSignature = Jwts.builder()
                .setSubject("test@test.fr")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS512, "wrongSecret")
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(tokenWithBadSignature);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should return false for malformed token")
    void shouldReturnFalseForMalformedToken() {
        String malformedToken = "invalid.jwt.token";

        boolean isValid = jwtUtils.validateJwtToken(malformedToken);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should return false for expired token")
    void shouldReturnFalseForExpiredToken() {
        String expiredToken = Jwts.builder()
                .setSubject("test@test.fr")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(SignatureAlgorithm.HS512, testSecret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(expiredToken);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should return false for unsupported token")
    void shouldReturnFalseForUnsupportedToken() {
        String unsupportedToken = Jwts.builder()
                .setSubject("test@test.fr")
                .compact();

        
        boolean isValid = jwtUtils.validateJwtToken(unsupportedToken);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should return false for empty claims")
    void shouldReturnFalseForEmptyClaims() {
        String emptyToken = "";

        boolean isValid = jwtUtils.validateJwtToken(emptyToken);

        assertThat(isValid).isFalse();
    }
}
