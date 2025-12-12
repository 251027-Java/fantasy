package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import dev.revature.fantasy.dto.LeagueStatsDto;
import dev.revature.fantasy.dto.LoginDto;
import dev.revature.fantasy.dto.RosterUserDto;
import dev.revature.fantasy.dto.StatDto;
import dev.revature.fantasy.exception.HttpConnectionException;
import dev.revature.fantasy.exception.InvalidUsernameException;
import dev.revature.fantasy.model.League;
import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.model.User;
import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.service.statsmodel.LuckData;
import dev.revature.fantasy.sleeperrequest.ResponseFormatter;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperNFLStateResponse;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperRosterUserResponse;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperUserResponse;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperUsernameResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class FantasyStatsServiceTest {
    @Mock
    LeagueService leagueService;

    @Mock
    UserService userService;

    @Mock
    DatabaseFormatterService databaseFormatterService;

    @Mock
    WeekScoreService weekScoreService;

    @Mock
    StatsComputationService statsComputationService;

    @Mock
    RosterUserService rosterUserService;

    @Mock
    ResponseFormatter responseFormatter;

    @InjectMocks
    FantasyStatsService fantasyStatsService;

    MockedStatic<ResponseFormatter> mockRf;
    MockedStatic<DatabaseFormatterService> mockDfs;

    @BeforeEach
    void setUp() {
        mockRf = mockStatic(ResponseFormatter.class);
        mockDfs = mockStatic(DatabaseFormatterService.class);
    }

    @AfterEach
    void tearDown() {
        mockRf.close();
        mockDfs.close();
    }

    @Test
    void login_nonExistantUsername_emptyDto() throws InvalidUsernameException, HttpConnectionException {
        String username = "asd";
        mockRf.when(() -> ResponseFormatter.getUserIdFromUsername(username)).thenReturn(null);

        Optional<LoginDto> realLoginDto = fantasyStatsService.login(username);

        assertTrue(realLoginDto.isEmpty());
    }

    @Test
    void login_newUserInDatabase_saveUserLeaguesAndReturnLoginDto()
            throws InvalidUsernameException, HttpConnectionException {
        String username = "asd";
        String userId = "123";

        mockRf.when(() -> ResponseFormatter.getUserIdFromUsername(username))
                .thenReturn(new SleeperUsernameResponse(userId));
        mockRf.when(() -> ResponseFormatter.getLeaguesFromUserId(userId)).thenReturn(List.of());

        League mockLeague = new League();
        mockLeague.setId("1234");
        mockLeague.setName("a league");

        List<League> mockLeagues = List.of(mockLeague);
        mockDfs.when(() -> DatabaseFormatterService.formatLeagueInfo(List.of())).thenReturn(mockLeagues);
        // end of arrange

        Optional<LoginDto> realLoginDto = fantasyStatsService.login(username);

        assertTrue(realLoginDto.isPresent());
        verify(leagueService, times(1)).idempotentSave(mockLeagues);
    }

    @Test
    void computeStats_validLeagueIdWithAllExistingWeekScoresInDatabase_returnsLeagueStatsEarly() {
        String leagueId = "asd";
        int numWeeksToCompute = 1;

        SleeperNFLStateResponse nflState = new SleeperNFLStateResponse();
        nflState.setDisplayWeek("2");

        mockRf.when(ResponseFormatter::getNFLState).thenReturn(nflState);

        int leagueRosters = 1;
        when(leagueService.getSizeOfLeague(leagueId)).thenReturn(leagueRosters);

        List<List<WeekScore>> weekScoresInDatabase = List.of(List.of(new WeekScore()));
        when(weekScoreService.findWeekScoresByLeagueId(leagueId, numWeeksToCompute))
                .thenReturn(weekScoresInDatabase);

        List<RosterUser> rosterUsers = List.of(new RosterUser());
        when(rosterUserService.getAllRosterUsersByLeagueId(leagueId)).thenReturn(rosterUsers);

        // for method weekScoresToStatsDto
        LuckData luckData = null;
        when(statsComputationService.computeStats(rosterUsers, weekScoresInDatabase))
                .thenReturn(luckData);
        when(statsComputationService.toDto(eq(luckData), anyMap())).thenReturn(new LeagueStatsDto(new StatDto[] {}));
        // end of arrange

        Optional<LeagueStatsDto> realLeagueStatsDto = fantasyStatsService.computeStats(leagueId);

        assertTrue(realLeagueStatsDto.isPresent());
    }

    @Test
    void computeStats_leagueIdWithSomeExistingWeekScoresInDatabaseButNowDeletedInSleeper_returnsEmpty() {
        String leagueId = "asd";
        int numWeeksToCompute = 1;

        SleeperNFLStateResponse nflState = new SleeperNFLStateResponse();
        nflState.setDisplayWeek("2");

        mockRf.when(ResponseFormatter::getNFLState).thenReturn(nflState);

        int leagueRosters = 2;
        when(leagueService.getSizeOfLeague(leagueId)).thenReturn(leagueRosters);

        List<List<WeekScore>> weekScoresInDatabase = List.of(List.of(new WeekScore()));
        when(weekScoreService.findWeekScoresByLeagueId(leagueId, numWeeksToCompute))
                .thenReturn(weekScoresInDatabase);

        mockRf.when(() -> ResponseFormatter.getUsersFromLeague(leagueId)).thenReturn(List.of());
        // end of arrange

        Optional<LeagueStatsDto> realLeagueStatsDto = fantasyStatsService.computeStats(leagueId);

        assertTrue(realLeagueStatsDto.isEmpty());
    }

    @Test
    void computeStats_newLeagueId_returnsLeagueStats() {
        String leagueId = "asd";
        int numWeeksToCompute = 1;

        SleeperNFLStateResponse nflState = new SleeperNFLStateResponse();
        nflState.setDisplayWeek("2");

        mockRf.when(ResponseFormatter::getNFLState).thenReturn(nflState);

        int leagueRosters = -1;
        when(leagueService.getSizeOfLeague(leagueId)).thenReturn(leagueRosters);

        List<List<WeekScore>> weekScoresInDatabase = List.of(List.of());
        when(weekScoreService.findWeekScoresByLeagueId(leagueId, numWeeksToCompute))
                .thenReturn(weekScoresInDatabase);

        List<SleeperUserResponse> sleeperUserResponses = List.of(new SleeperUserResponse());
        mockRf.when(() -> ResponseFormatter.getUsersFromLeague(leagueId)).thenReturn(sleeperUserResponses);

        List<User> users = List.of(new User());
        mockDfs.when(() -> DatabaseFormatterService.formatUsers(sleeperUserResponses))
                .thenReturn(users);

        List<SleeperRosterUserResponse> sleeperRosterUserResponses = List.of(new SleeperRosterUserResponse());
        mockRf.when(() -> ResponseFormatter.getRostersFromLeagueId(leagueId)).thenReturn(sleeperRosterUserResponses);

        List<RosterUserDto> rosterUserDtos = List.of();
        mockRf.when(() -> DatabaseFormatterService.formatRosterUsers(sleeperRosterUserResponses))
                .thenReturn(rosterUserDtos);

        List<RosterUser> rosterUsers = List.of(new RosterUser());
        when(rosterUserService.upsertUsers(rosterUserDtos)).thenReturn(rosterUsers);

        int numWeeksFound = weekScoresInDatabase.size();
        List<WeekScore> weekScores = List.of(new WeekScore());
        when(weekScoreService.concurrentGetWeekScores(leagueId, numWeeksFound, numWeeksToCompute))
                .thenReturn(weekScores);

        List<List<WeekScore>> updatedWeekScores = List.of(List.of(new WeekScore()));
        when(weekScoreService.findWeekScoresByLeagueId(leagueId, numWeeksToCompute))
                .thenReturn(updatedWeekScores);

        // for method weekScoresToStatsDto
        LuckData luckData = null;
        when(statsComputationService.computeStats(rosterUsers, updatedWeekScores))
                .thenReturn(luckData);
        when(statsComputationService.toDto(eq(luckData), anyMap())).thenReturn(new LeagueStatsDto(new StatDto[] {}));
        // end of arrange

        Optional<LeagueStatsDto> realLeagueStatsDto = fantasyStatsService.computeStats(leagueId);

        assertTrue(realLeagueStatsDto.isPresent());
        verify(userService, times(1)).idempotentSave(users);
        verify(weekScoreService, times(1)).upsertWeekScores(weekScores);
    }
}
