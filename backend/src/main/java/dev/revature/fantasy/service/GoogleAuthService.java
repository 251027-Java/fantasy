package dev.revature.fantasy.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import dev.revature.fantasy.dto.AuthResponseDto;
import dev.revature.fantasy.logger.GlobalLogger;
import dev.revature.fantasy.dto.AuthRequestDto;

@Service
public class GoogleAuthService implements AuthService {
    
    private final String clientId;

    public GoogleAuthService(@Value("${google.client.id:MISSING}") String clientId) {
        this.clientId = clientId;
        GlobalLogger.debug("Client ID: " + clientId);
    }

    public Optional<AuthResponseDto> auth(AuthRequestDto authRequestDto) {
        GoogleIdToken.Payload payload = verifyToken(authRequestDto.getToken());

        if (payload == null) {
            return Optional.empty();
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        AuthResponseDto response = new AuthResponseDto(email, name);

        return Optional.of(response);
    }


    private GoogleIdToken.Payload verifyToken(String tokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(tokenString);

            if (idToken != null) {
                return idToken.getPayload(); // Token is valid, return user info
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Token invalid
    }
        
}
