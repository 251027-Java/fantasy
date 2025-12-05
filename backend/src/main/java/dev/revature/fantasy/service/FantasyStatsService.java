package dev.revature.fantasy.service;

import dev.revature.fantasy.dto.*;
import dev.revature.fantasy.exception.*;
import dev.revature.fantasy.model.*;
import dev.revature.fantasy.service.statsmodel.LuckData;
import dev.revature.fantasy.sleeperrequest.ResponseFormatter;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            RosterUserService rosterUserService) {
        this.leagueService = leagueService;
        this.userService = userService;
        this.databaseFormatterService = databaseFormatterService;
        this.weekScoreService = weekScoreService;
        this.statsComputationService = statsComputationService;
        this.rosterUserService = rosterUserService;
    }

    /**
     * Run the login endpoint logic, attempting to get the leagues
     * from a sleeper username
     * @param usernameStr the sleeper username
     * @return the login response, empty if the username is invalid
     * @throws HttpConnectionException when one of the sleeper requests fails
     */
    public Optional<LoginDto> login(String usernameStr) throws HttpConnectionException, InvalidUsernameException {

        SleeperUsernameResponse usernameResponse = ResponseFormatter.getUserIdFromUsername(usernameStr);
        // if username not found
        if (usernameResponse == null) {
            return Optional.empty();
        }

        List<SleeperLeagueResponse> sleeperLeagues =
                ResponseFormatter.getLeaguesFromUserId(usernameResponse.getUserId());
        // convert league responses to database format
        List<League> databaseLeagues = DatabaseFormatterService.formatLeagueInfo(sleeperLeagues);
        // save to database
        this.leagueService.idempotentSave(databaseLeagues);
        // TODO: think about why we do/don't need the result here

        // convert to dto (LoginResponse)
        LeagueDto[] leagueResponses = databaseLeagues.stream()
                .map(league -> new LeagueDto(league.getLeagueId(), league.getLeagueName()))
                .toArray(LeagueDto[]::new);

        LoginDto loginResponse = new LoginDto(usernameResponse.getUserId(), leagueResponses);

        return Optional.of(loginResponse);
    }

    /**
     * Run the compute luck stats endpoint logic
     * @param leagueId the sleeper league id to get the stats for
     * @return the league stats dto, not sure when this would/should be empty
     */
    public Optional<LeagueStatsDto> computeStats(String leagueId) {
        // make sleeper request with leagueId to get users
        List<SleeperUserResponse> sleeperUsers = ResponseFormatter.getUsersFromLeague(leagueId);
        if (sleeperUsers.size() == 0) {
            return Optional.empty();
        }
        // convert user responses to database format
        List<User> databaseUsers = DatabaseFormatterService.formatUsers(sleeperUsers);

        // save to database
        this.userService.idempotentSave(databaseUsers);

        // get rosters from leagueId
        List<SleeperRosterUserResponse> sleeperRosterUsers = ResponseFormatter.getRostersFromLeagueId(leagueId);
        List<RosterUserDto> rosterUserDtos = DatabaseFormatterService.formatRosterUsers(sleeperRosterUsers);

        // persist to database, need to upsert incase a users' stats (wins, etc) changed
        List<RosterUser> rosterUsers = this.rosterUserService.upsertUsers(rosterUserDtos);

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
