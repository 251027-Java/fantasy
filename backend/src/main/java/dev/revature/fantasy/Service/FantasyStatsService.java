package dev.revature.fantasy.service;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

import dev.revature.fantasy.dto.*;
import dev.revature.fantasy.exceptions.HttpConnectionException;
import dev.revature.fantasy.exceptions.*;
import dev.revature.fantasy.model.League;
import dev.revature.fantasy.model.User;
import dev.revature.fantasy.sleeperRequest.ResponseFormatter;
import dev.revature.fantasy.sleeperRequest.SleeperRequestHandler;
import dev.revature.fantasy.sleeperRequest.sleeperResponseModels.SleeperLeagueResponse;
import dev.revature.fantasy.sleeperRequest.sleeperResponseModels.SleeperUsernameResponse;

public class FantasyStatsService {

    // run the login endpoint 
    //     pipeline for login
    // - make sleeper request with username
    // - convert response 
    // - make sleeper request with obtained userId
    // - make sleeper request with userId to get leagues
    // - convert response to db format (dto)
    // - persist to database
    // - return response to as json to endpoint

    /**
     * Run the login endpoint logic, attempting to get the leagues
     * from a sleeper username
     * @param usernameStr the sleeper username
     * @return the login response
     * @throws HttpConnectionException when one of the sleeper requests fails
     */
    public static Optional<LoginDto> login(String usernameStr) throws HttpConnectionException, InvalidUsernameException {


        SleeperUsernameResponse usernameResponse = ResponseFormatter.getUserIdFromUsername(usernameStr);
        // if username not found
        if (usernameResponse == null) {
            return Optional.empty();
        }

        List<SleeperLeagueResponse> sleeperLeagues = ResponseFormatter.getLeaguesFromUserId(usernameResponse.getUserId());
        // convert league responses to database format
        List<League> databaseLeagues = DatabaseFormatter.formatLeagueInfo(sleeperLeagues);
        // convert to dto (LoginResponse)
        LeagueDto[] leagueResponses = databaseLeagues.stream()
            .map(league -> new LeagueDto(league.getLeagueId(), league.getLeagueName()))
            .toArray(LeagueDto[]::new);

        LoginDto loginResponse = new LoginDto(usernameResponse.getUserId(), leagueResponses);

        return Optional.of(loginResponse);
    }

    // run the compute stats endpoint
    public static LeagueStatsDto computeStats(String leagueId) {
        return null;
    }
    
}
