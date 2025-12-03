package dev.revature.fantasy.service;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dev.revature.fantasy.dto.*;
import dev.revature.fantasy.exceptions.*;
import dev.revature.fantasy.model.*;
import dev.revature.fantasy.service.statsModels.Stats;
import dev.revature.fantasy.service.statsModels.LuckData;
import dev.revature.fantasy.sleeperRequest.ResponseFormatter;
import dev.revature.fantasy.sleeperRequest.SleeperRequestHandler;
import dev.revature.fantasy.sleeperRequest.sleeperResponseModels.*;

@Service 
public class FantasyStatsService {
    private final LeagueService leagueService;
    private final UserService userService;
    private final DatabaseFormatterService databaseFormatterService;
    private final WeekScoreService weekScoreService;
    private final StatsComputationService statsComputationService;
    private final RosterUserService rosterUserService;

    public FantasyStatsService(
        LeagueService leagueService,
        UserService userService,
        DatabaseFormatterService databaseFormatterService,
        WeekScoreService weekScoreService,
        StatsComputationService statsComputationService,
        RosterUserService rosterUserService
    ) {
        this.leagueService = leagueService;
        this.userService = userService;
        this.databaseFormatterService = databaseFormatterService;
        this.weekScoreService = weekScoreService;
        this.statsComputationService = statsComputationService;
        this.rosterUserService = rosterUserService;
    }

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
    public Optional<LoginDto> login(String usernameStr) throws HttpConnectionException, InvalidUsernameException {


        SleeperUsernameResponse usernameResponse = ResponseFormatter.getUserIdFromUsername(usernameStr);
        // if username not found
        if (usernameResponse == null) {
            return Optional.empty();
        }

        List<SleeperLeagueResponse> sleeperLeagues = ResponseFormatter.getLeaguesFromUserId(usernameResponse.getUserId());
        // convert league responses to database format
        List<League> databaseLeagues = DatabaseFormatterService.formatLeagueInfo(sleeperLeagues);
        // save to database
        List<League> _ = this.leagueService.idempotentSave(databaseLeagues);  
        // TODO: think about why we do/don't need the result here

        // convert to dto (LoginResponse)
        LeagueDto[] leagueResponses = databaseLeagues.stream()
            .map(league -> new LeagueDto(league.getLeagueId(), league.getLeagueName()))
            .toArray(LeagueDto[]::new);

        LoginDto loginResponse = new LoginDto(usernameResponse.getUserId(), leagueResponses);

        return Optional.of(loginResponse);
    }

    // run the compute stats endpoint
    public Optional<LeagueStatsDto> computeStats(String leagueId) {
        // make sleeper request with leagueId to get users
        List<SleeperUserResponse> sleeperUsers = ResponseFormatter.getUsersFromLeague(leagueId);
        // convert user responses to database format
        List<User> databaseUsers = DatabaseFormatterService.formatUsers(sleeperUsers);

        // save to database
        var _ = this.userService.idempotentSave(databaseUsers);

        // get rosters from leagueId
        List<SleeperRosterUserResponse> sleeperRosterUsers = ResponseFormatter.getRostersFromLeagueId(leagueId);
        List<RosterUserDto> rosterUserDtos = DatabaseFormatterService.formatRosterUsers(sleeperRosterUsers);

        // persist to database
        List<RosterUser> rosterUsers = this.rosterUserService.saveUsers(rosterUserDtos);

        // TODO: implement a way to have league specific timestamps when data was persisted
        // to 'cache' result

        // get the nfl state info from sleeper
        SleeperNFLStateResponse nflState = ResponseFormatter.getNFLState();
        int currentWeek = Integer.parseInt(nflState.getDisplayWeek());
        int currSeason = Integer.parseInt(nflState.getDisplayWeek());

        // make matchup requests for each week from sleeper 
        List<WeekScore> weekScores = new ArrayList<>();
        for (int week = 1; week < currentWeek; week++) {
            var matchups = ResponseFormatter.getMatchupsFromLeagueIdAndWeek(leagueId, week);
            // convertTo WeekScores for computation
            var scores = this.databaseFormatterService.formatMatchups(matchups, leagueId, week);
            
            weekScores.addAll(scores);

        }
        // persist weekscores all at once with idempotency
        this.weekScoreService.upsertWeekScores(weekScores);

        // get weekscores from database
        List<List<WeekScore>> allWeekScores = this.weekScoreService.findWeekScoresByLeagueId(leagueId, currentWeek);

        // do stats computation
        // need the rosterUserIds
        LuckData luckData = this.statsComputationService.computeStats(rosterUsers, allWeekScores);

        // get the names of the roster users
        Map<Long, String> rosterUserIdToName = this.rosterUserService.getRosterUserIdToName(rosterUsers);

        // convert to dto for response
        LeagueStatsDto leagueStatsDto = this.statsComputationService.toDto(luckData, rosterUserIdToName);

        return Optional.of(leagueStatsDto);

    }
    
}
