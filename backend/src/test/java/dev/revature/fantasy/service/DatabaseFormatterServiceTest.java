package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperMatchupResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DatabaseFormatterServiceTest {
    @Mock
    RosterUserService rosterUserService;

    @InjectMocks
    DatabaseFormatterService databaseFormatterService;

    @Test
    void formatMatchups_validResponseWithLeagueIdAndWeek_returnsTheRespectiveMatchup() {
        String leagueId = "asd";
        int week = 1;

        SleeperMatchupResponse sleeperMatchupResponse = new SleeperMatchupResponse();
        RosterUser rosterUser = new RosterUser();

        when(rosterUserService.getRosterUserByRosterIdAndLeagueId(sleeperMatchupResponse.getRosterId(), leagueId))
                .thenReturn(Optional.of(rosterUser));

        List<WeekScore> matchups =
                databaseFormatterService.formatMatchups(List.of(sleeperMatchupResponse), leagueId, week);

        assertEquals(1, matchups.size());
    }
}
