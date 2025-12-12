package dev.revature.fantasy.service;

import dev.revature.fantasy.dto.LeagueStatsDto;
import dev.revature.fantasy.dto.LoginDto;
import dev.revature.fantasy.dto.StatDto;
import dev.revature.fantasy.exception.HttpConnectionException;
import dev.revature.fantasy.exception.InvalidUsernameException;
import dev.revature.fantasy.model.League;
import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.service.statsmodel.LuckData;
import dev.revature.fantasy.sleeperrequest.ResponseFormatter;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperNFLStateResponse;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

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

        Optional<LoginDto> realLoginDto = fantasyStatsService.login(username);

        assertTrue(realLoginDto.isPresent());
        verify(leagueService, times(1)).idempotentSave(mockLeagues);
    }

    @Test
    void computeStats_validLeagueIdWithAllExistingWeekScoresInDatabase_returnsLeagueStatsEarly() {
        String leagueId = "asd";
        int maxWeekToSearch = 1;

        SleeperNFLStateResponse nflState = new SleeperNFLStateResponse();
        nflState.setDisplayWeek("" + (maxWeekToSearch + 1));

        mockRf.when(ResponseFormatter::getNFLState).thenReturn(nflState);

        int leagueRosters = 1;
        List<List<WeekScore>> weekScoresInDatabase = List.of(List.of(new WeekScore()));
        when(leagueService.getSizeOfLeague(leagueId)).thenReturn(leagueRosters);
        when(weekScoreService.findWeekScoresByLeagueId(leagueId, maxWeekToSearch))
                .thenReturn(weekScoresInDatabase);

        List<RosterUser> rosterUsers = List.of(new RosterUser());
        when(rosterUserService.getAllRosterUsersByLeagueId(leagueId)).thenReturn(rosterUsers);

        LuckData luckData = null;
        when(statsComputationService.computeStats(rosterUsers, weekScoresInDatabase))
                .thenReturn(luckData);
        when(statsComputationService.toDto(eq(luckData), anyMap())).thenReturn(new LeagueStatsDto(new StatDto[] {}));

        Optional<LeagueStatsDto> realLeagueStatsDto = fantasyStatsService.computeStats(leagueId);

        assertTrue(realLeagueStatsDto.isPresent());
    }

    @Test
    void computeStats_leagueIdWithSomeExistingWeekScoresInDatabaseButNowDeletedInSleeper_returnsEmpty() {
        String leagueId = "asd";
        int maxWeekToSearch = 1;

        SleeperNFLStateResponse nflState = new SleeperNFLStateResponse();
        nflState.setDisplayWeek("" + (maxWeekToSearch + 1));

        mockRf.when(ResponseFormatter::getNFLState).thenReturn(nflState);

        int leagueRosters = 2;
        List<List<WeekScore>> weekScoresInDatabase = List.of(List.of(new WeekScore()));
        when(leagueService.getSizeOfLeague(leagueId)).thenReturn(leagueRosters);
        when(weekScoreService.findWeekScoresByLeagueId(leagueId, maxWeekToSearch))
                .thenReturn(weekScoresInDatabase);

        mockRf.when(() -> ResponseFormatter.getUsersFromLeague(leagueId)).thenReturn(List.of());

        Optional<LeagueStatsDto> realLeagueStatsDto = fantasyStatsService.computeStats(leagueId);

        assertTrue(realLeagueStatsDto.isEmpty());
    }
}
