package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import dev.revature.fantasy.dto.LoginDto;
import dev.revature.fantasy.exception.HttpConnectionException;
import dev.revature.fantasy.exception.InvalidUsernameException;
import dev.revature.fantasy.model.League;
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
    void computeStats_validLeagueId_() {
        SleeperNFLStateResponse nflState = new SleeperNFLStateResponse();
        nflState.setDisplayWeek("displayweek");

        mockRf.when(ResponseFormatter::getNFLState).thenReturn(nflState);
    }
}
