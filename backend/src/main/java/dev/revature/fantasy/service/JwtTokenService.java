package dev.revature.fantasy.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class JwtTokenService {

    // Inject the secret key from your .env file
    private final String secret;
    private final long EXPIRATION_TIME;

    public JwtTokenService(
            @Value("${JWT_SECRET}") String secret, @Value("${JWT_EXPIRATION_TIME_MILI}") long expirationTime) {
        this.secret = secret;
        this.EXPIRATION_TIME = expirationTime;
    }

    // Helper method to get the signing key
    private SecretKey getSigningKey() {
        // Decode the Base64-encoded secret key
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a new JWT for the authenticated user.
     * This token will be sent to the frontend for future authorization.
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            var expiryDate = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiryDate.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // You would also include methods here to validate and extract claims from a token
}
