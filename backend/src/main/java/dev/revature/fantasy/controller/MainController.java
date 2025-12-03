package dev.revature.fantasy.controller;

import dev.revature.fantasy.dto.*;
import dev.revature.fantasy.exceptions.HttpConnectionException;
import dev.revature.fantasy.exceptions.InvalidLeagueIdException;
import dev.revature.fantasy.exceptions.InvalidUsernameException;
import dev.revature.fantasy.service.FantasyStatsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class MainController {

    private final FantasyStatsService fantasyStatsService;

    public MainController(FantasyStatsService fantasyStatsService) {
        this.fantasyStatsService = fantasyStatsService;
    }


    @GetMapping("/login/{username}")
    public LoginDto getUserInfo(@PathVariable String username) throws HttpConnectionException, InvalidUsernameException {
        // TODO: send 406 status code if there are no leagues
        return this.fantasyStatsService.login(username)
        .orElseThrow(() -> new InvalidUsernameException("Invalid username: " + username));
    }

    @GetMapping("/league/{id}/stats")
    public LeagueStatsDto getLeagueStats(@PathVariable String id) throws HttpConnectionException, InvalidLeagueIdException {
        return this.fantasyStatsService.computeStats(id)
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