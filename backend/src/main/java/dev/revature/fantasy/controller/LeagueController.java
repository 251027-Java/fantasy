package dev.revature.fantasy.controller;

import dev.revature.fantasy.dto.LeagueStatsDto;
import dev.revature.fantasy.dto.LoginDto;
import dev.revature.fantasy.exception.HttpConnectionException;
import dev.revature.fantasy.exception.InvalidLeagueIdException;
import dev.revature.fantasy.exception.InvalidUsernameException;
import dev.revature.fantasy.service.FantasyStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class LeagueController {

    private final FantasyStatsService fantasyStatsService;

    public LeagueController(FantasyStatsService fantasyStatsService) {
        this.fantasyStatsService = fantasyStatsService;
    }

    @Tag(name = "User")
    @Operation(
            description = "Retrieve user information associated with a username.",
            security = @SecurityRequirement(name = "auth"))
    @GetMapping("/info/{username}")
    public LoginDto getUserInfo(@PathVariable String username)
            throws HttpConnectionException, InvalidUsernameException {
        // TODO: send 406 status code if there are no leagues for the user
        return this.fantasyStatsService
                .login(username)
                .orElseThrow(() -> new InvalidUsernameException("Invalid username: " + username));
    }

    @Tag(name = "League")
    @Operation(description = "Retrieve stats for a league.", security = @SecurityRequirement(name = "auth"))
    @GetMapping("/league/{id}/stats")
    public LeagueStatsDto getLeagueStats(@PathVariable String id)
            throws HttpConnectionException, InvalidLeagueIdException {
        return this.fantasyStatsService
                .computeStats(id)
                .orElseThrow(() -> new InvalidLeagueIdException("Invalid league id: " + id));
    }

    // exception handler for http connection exceptions
    @ExceptionHandler(HttpConnectionException.class)
    public ResponseEntity<String> handleHttpConnectionException(HttpConnectionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<String> handleInvalidUsernameException(InvalidUsernameException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InvalidLeagueIdException.class)
    public ResponseEntity<String> handleInvalidLeagueIdException(InvalidLeagueIdException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
