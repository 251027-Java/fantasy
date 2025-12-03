package dev.revature.fantasy.controller;

import dev.revature.fantasy.dto.*;
import dev.revature.fantasy.exceptions.HttpConnectionException;
import dev.revature.fantasy.exceptions.InvalidUsernameException;
import dev.revature.fantasy.service.FantasyStatsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class MainController {


    @GetMapping("/login/{username}")
    public LoginDto getUserInfo(@PathVariable String username) throws HttpConnectionException, InvalidUsernameException {
        // TODO: send 406 status code if there are no leagues
        return FantasyStatsService.login(username)
        .orElseThrow(() -> new InvalidUsernameException("Invalid username: " + username));
    }

    @GetMapping("/league/{id}/stats")
    public LeagueStatsDto getLeagueStats(@PathVariable String id) {
        return null;
    }

    // exception handler for http connection exceptions
    @ExceptionHandler(HttpConnectionException.class)
    public ResponseEntity<String> handleHttpConnectionException(HttpConnectionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}