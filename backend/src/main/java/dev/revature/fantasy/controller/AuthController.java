package dev.revature.fantasy.controller;

import dev.revature.fantasy.dto.AuthRequestDto;
import dev.revature.fantasy.dto.AuthResponseDto;
import dev.revature.fantasy.exception.AuthException;
import dev.revature.fantasy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Send the token to google auth service to verify it
     */
    @Tag(name = "Auth")
    @Operation(description = "Authenticate with a Google OAuth code.")
    @PostMapping("/auth/google")
    public AuthResponseDto googleAuth(@RequestBody AuthRequestDto authRequestDto) {
        return this.authService.auth(authRequestDto).orElseThrow(() -> new AuthException("Authentication failed"));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
}
