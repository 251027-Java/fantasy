package dev.revature.fantasy.service;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import dev.revature.fantasy.dto.AuthRequestDto;
import dev.revature.fantasy.dto.AuthResponseDto;
import dev.revature.fantasy.logger.GlobalLogger;
import dev.revature.fantasy.model.AppUser;
import dev.revature.fantasy.repository.AppUserRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Service
public class GoogleAuthService implements AuthService {

    private final String clientId;
    private final String clientSecret;
    private final JwtTokenService tokenService;
    private final AppUserRepo appUserRepo;
    private final String redirectUri;

    public GoogleAuthService(
            @Value("${GOOGLE_CLIENT_ID:MISSING}") String clientId,
            @Value("${GOOGLE_CLIENT_SECRET:MISSING}") String clientSecret,
            @Value("${FRONTEND_BASE_URL:MISSING}") String redirectUri,
            JwtTokenService tokenService,
            AppUserRepo appUserRepo) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenService = tokenService;
        this.redirectUri = redirectUri;
        this.appUserRepo = appUserRepo;
        GlobalLogger.debug("Client ID: " + clientId);
        // GlobalLogger.debug("Client Secret: " + clientSecret);
    }

    public Optional<AuthResponseDto> auth(AuthRequestDto authRequestDto) {
        var codeTokenRequest = new CodeTokenRequestDto(clientId, clientSecret, authRequestDto.getCode(), redirectUri);
        var payload = verifyCode(codeTokenRequest);

        if (payload.isEmpty()) {
            return Optional.empty();
        }

        String email = payload.get().getEmail();
        String name = (String) payload.get().get("name");

        String token = tokenService.generateToken(email);

        AppUser user = new AppUser(email, name);
        this.appUserRepo.save(user);

        AuthResponseDto response = new AuthResponseDto(email, name, token);

        return Optional.of(response);
    }

    public static Optional<GoogleIdToken.Payload> verifyToken(TokenVerifyDto dto) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                            new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(dto.getClientId()))
                    .build();

            GoogleIdToken idToken = verifier.verify(dto.getToken());

            if (idToken != null) {
                return Optional.of(idToken.getPayload()); // Token is valid, return user info
            }
        } catch (Exception e) {
            GlobalLogger.error("Failed to verify google oauth code: " + e.getMessage());
        }
        return Optional.empty(); // Token invalid
    }

    public static Optional<GoogleIdToken.Payload> verifyCode(CodeTokenRequestDto dto) {
        try {
            GlobalLogger.debug("Code: " + dto.getCode());
            TokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                            new NetHttpTransport(),
                            new GsonFactory(),
                            dto.getClientId(),
                            dto.getClientSecret(),
                            dto.getCode(),
                            dto.getRedirectUri())
                    .execute();

            // GlobalLogger.debug("Token Response: " + tokenResponse.toPrettyString());

            // get access token from response
            var googleResponse = (GoogleTokenResponse) tokenResponse;

            // users verified identity
            GoogleIdToken idToken = googleResponse.parseIdToken();

            if (idToken != null) {
                return Optional.of(idToken.getPayload());
            }

        } catch (TokenResponseException e) {
            // this handles errors like 400 Bad Request, invalid_grant, invalid_code etc.
            GlobalLogger.error("Token Exchange Error (HTTP " + e.getStatusCode() + "): "
                    + e.getDetails().getError());
        } catch (IOException e) {
            // this handles network issues like connection timeouts or IO failures
            GlobalLogger.error("IO Error during token exchange: " + e.getMessage());
        } catch (Exception e) {
            // catch-all for other unexpected issues
            GlobalLogger.error("Failed to verify google oauth code: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenVerifyDto {
        private String clientId;
        private String token;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeTokenRequestDto {
        private String clientId;
        private String clientSecret;
        private String code;
        private String redirectUri;
    }
}
