package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtTokenServiceTest {

    JwtTokenService service;

    @BeforeEach
    void setUp() {
        String secret = "a".repeat(100);
        long expirationTime = 10;
        service = new JwtTokenService(secret, expirationTime);
    }

    @Test
    void generateToken_validEmail_generatesTokenFromEmail() {
        String email = "asd@gmail.com";

        String token = service.generateToken(email);

        assertNotNull(token);
    }

    @Test
    void validateToken_invalidTaken_returnsFalse() {
        String token = "asdiasdo";

        boolean valid = service.validateToken(token);

        assertFalse(valid);
    }
}
