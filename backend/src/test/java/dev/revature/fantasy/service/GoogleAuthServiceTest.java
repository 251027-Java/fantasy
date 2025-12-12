package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import dev.revature.fantasy.dto.AuthRequestDto;
import dev.revature.fantasy.dto.AuthResponseDto;
import dev.revature.fantasy.model.AppUser;
import dev.revature.fantasy.repository.AppUserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GoogleAuthServiceTest {
    @Mock
    JwtTokenService jwtTokenService;

    @Mock
    AppUserRepo appUserRepo;

    @InjectMocks
    GoogleAuthService service;

    MockedStatic<GoogleAuthService> mockGas;

    @BeforeEach
    void setUp() {
        mockGas = mockStatic(GoogleAuthService.class);
    }

    @AfterEach
    void tearDown() {
        mockGas.close();
    }

    @Test
    void auth_invalidRequestDetails_returnsEmptyResponse() {
        mockGas.when(() -> GoogleAuthService.verifyCode(any(GoogleAuthService.CodeTokenRequestDto.class)))
                .thenReturn(Optional.empty());
        AuthRequestDto authRequestDto = new AuthRequestDto();

        Optional<AuthResponseDto> authResponseDto = service.auth(authRequestDto);

        assertTrue(authResponseDto.isEmpty());
    }

    @Test
    void auth_validRequestDetails_returnsResponse() {
        String email = "some@email.com";

        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail(email);
        payload.set("name", "my name");

        mockGas.when(() -> GoogleAuthService.verifyCode(any(GoogleAuthService.CodeTokenRequestDto.class)))
                .thenReturn(Optional.of(payload));

        String token = "thetoken";
        when(jwtTokenService.generateToken(email)).thenReturn(token);

        AuthRequestDto authRequestDto = new AuthRequestDto();
        // end of arrange

        Optional<AuthResponseDto> authResponseDto = service.auth(authRequestDto);

        assertTrue(authResponseDto.isPresent());

        assertEquals("some@email.com", authResponseDto.get().getEmail());
        assertEquals("my name", authResponseDto.get().getName());
        assertEquals("thetoken", authResponseDto.get().getJwtToken());

        verify(appUserRepo, times(1)).save(any(AppUser.class));
    }
}
