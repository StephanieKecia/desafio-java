package desafio.java.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        String token = jwtService.generateToken("user", "USER");

        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("user", username);
    }

    @Test
    void testIsTokenValid_ValidToken() {
        String token = jwtService.generateToken("admin", "ADMIN");

        boolean valid = jwtService.isTokenValid(token, "admin");
        assertTrue(valid);
    }

    @Test
    void testIsTokenValid_InvalidUsername() {
        String token = jwtService.generateToken("admin", "ADMIN");

        boolean valid = jwtService.isTokenValid(token, "user");
        assertFalse(valid);
    }

    @Test
    void testRefreshToken() throws InterruptedException {
        String token = jwtService.generateToken("user", "USER");

        String refreshedToken = jwtService.refreshToken(token);

        assertNotNull(refreshedToken);
        assertEquals(token, refreshedToken);
        assertEquals("user", jwtService.extractUsername(refreshedToken));
        assertTrue(jwtService.isTokenValid(refreshedToken, "user"));
    }

    @Test
    void testTokenExpiration() {
        String expiredToken = Jwts.builder()
                .setSubject("userExpired")
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, "chaveSuperSecreta@2025!#jwt")
                .compact();

        JwtService jwtService = new JwtService();

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.extractUsername(expiredToken);
        });
    }
}

