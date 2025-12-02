package dev.revature.fantasy.Service;

import dev.revature.fantasy.ResponseModels.LoginResponse;
import dev.revature.fantasy.ResponseModels.StatsResponse;
import dev.revature.fantasy.SleeperRequest.SleeperRequestHandler;

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

    public static LoginResponse login(String username) {
        var user = SleeperRequestHandler.getUserFromUsername(username);
        return null;
    }

    // run the compute stats endpoint
    public static StatsResponse computeStats(String leagueId) {
        return null;
    }
    
}
