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
     * 
     * @param usernameStr the sleeper username
     * @return the login response, empty if the username is invalid
     * @throws HttpConnectionException when one of the sleeper requests fails
     */

    // Returns a LoginDto object after getting the leagues from a given username.
    // Nothing returned if username is invalid
    // Uses returned SleeperUsernameResponse to get userId, then uses that to get
    // leagues associated with that userId along with users name
    public Optional<LoginDto> login(String usernameStr) throws HttpConnectionException, InvalidUsernameException {

        // Recieve an HTTP response from sleeper for the given username as a
        // SleeperUsernameResponse object (JSON userID)
        SleeperUsernameResponse usernameResponse = ResponseFormatter.getUserIdFromUsername(usernameStr);
        // if username not found
        if (usernameResponse == null) {
            return Optional.empty();
        }

        List<SleeperLeagueResponse> sleeperLeagues = ResponseFormatter
                .getLeaguesFromUserId(usernameResponse.getUserId());
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
     * Run the compute luck stats endpoint logic. Requires that the leagueId be
     * valid
     * and already in the database.
     * 
     * @param leagueId the sleeper league id to get the stats for
     * @return the league stats dto, not sure when this would/should be empty
     */
    public Optional<LeagueStatsDto> computeStats(String leagueId) {
        // TODO: check if there are weekscores already in database for this league
        // and that there are the correct amount of weeks, and users

        // get the nfl state info from sleeper
        SleeperNFLStateResponse nflState = ResponseFormatter.getNFLState();
        int currentWeek = Integer.parseInt(nflState.getDisplayWeek());
        int currSeason = Integer.parseInt(nflState.getDisplayWeek());
        int numWeeksToCompute = currentWeek - 1;

        int sizeOfLeague = this.leagueService.getSizeOfLeague(leagueId);

        // see if weekscores already in database for this league
        List<List<WeekScore>> weekScores = this.weekScoreService.findWeekScoresByLeagueId(leagueId, numWeeksToCompute);
        int numWeeksFound = weekScores.size();

        if (numWeeksFound == numWeeksToCompute) {
            boolean allWeeksFound = true;
            for (List<WeekScore> weekScoreList : weekScores) {
                if (weekScoreList.size() != sizeOfLeague) {
                    allWeeksFound = false;
                    break;
                }
            }
            if (allWeeksFound) { // all weeks found, just need roster to return
                List<RosterUser> rosterUsers = this.rosterUserService.getAllRosterUsersByLeagueId(leagueId);
                var optionalStatsDto = this.weekScoresToStatsDto(weekScores, rosterUsers);
                return optionalStatsDto;
            }
        }

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

        // make matchup requests for each week from sleeper
        // starting from the first week we don't have
        weekScores.clear();
        List<WeekScore> weekScoresToPersist = new ArrayList<>();
        for (int week = numWeeksFound + 1; week <= numWeeksToCompute; week++) {
            var matchups = ResponseFormatter.getMatchupsFromLeagueIdAndWeek(leagueId, week);
            // convertTo WeekScores for computation
            var scores = this.databaseFormatterService.formatMatchups(matchups, leagueId, week);

            weekScoresToPersist.addAll(scores);
        }
        // persist weekscores all at once with idempotency
        this.weekScoreService.upsertWeekScores(weekScoresToPersist);

        // get weekscores from database
        List<List<WeekScore>> allWeekScores = this.weekScoreService.findWeekScoresByLeagueId(leagueId,
                numWeeksToCompute);

        // do stats computation
        // need the rosterUserIds
        return this.weekScoresToStatsDto(allWeekScores, rosterUsers);
    }

    /**
     * Does the stats computation and returns the stats dto for stats endpoint
     * response. Requires that the parameters are compatible, ie that the weekScores
     * reference the same rosterUsers.
     * 
     * @param weekScores  the weekscores for the league
     * @param rosterUsers the rosters for the league
     */
    private Optional<LeagueStatsDto> weekScoresToStatsDto(
            List<List<WeekScore>> weekScores, List<RosterUser> rosterUsers) {
        LuckData luckData = this.statsComputationService.computeStats(rosterUsers, weekScores);

        // get the names of the roster users
        Map<Long, String> rosterUserIdToName = this.rosterUserService.getRosterUserIdToName(rosterUsers);

        // convert to dto for response
        LeagueStatsDto leagueStatsDto = this.statsComputationService.toDto(luckData, rosterUserIdToName);

        return Optional.of(leagueStatsDto);
    }
}
