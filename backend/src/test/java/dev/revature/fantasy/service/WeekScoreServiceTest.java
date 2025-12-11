package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import dev.revature.fantasy.model.*;
import dev.revature.fantasy.repository.WeekScoreRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class WeekScoreServiceTest {
    @Mock
    WeekScoreRepo repo;

    @InjectMocks
    WeekScoreService service;

    WeekScore createMockWeekScore() {
        League league = new League();
        User user = new User();

        RosterUser rosterUser = new RosterUser();
        rosterUser.setUser(user);
        rosterUser.setLeague(league);

        WeekScoreId weekScoreId = new WeekScoreId();
        weekScoreId.setRosterUser(rosterUser);

        WeekScore weekScore = new WeekScore();
        weekScore.setId(weekScoreId);

        return weekScore;
    }

    @Test
    void testSavesAllWeekScores() {
        List<WeekScore> mockWeekScores = List.of(createMockWeekScore());

        service.upsertWeekScores(mockWeekScores);

        verify(repo, times(1)).saveAll(mockWeekScores);
    }

    @Test
    void testObtainWeekScoresByLeagueIdUpToMaxWeek() {
        String leagueId = "someLeagueId";

        WeekScore w1 = createMockWeekScore();
        WeekScore w2 = createMockWeekScore();
        WeekScore w3 = createMockWeekScore();

        w1.getId().getRosterUser().getLeague().setId(leagueId);
        w2.getId().getRosterUser().getLeague().setId(leagueId);
        w3.getId().getRosterUser().getLeague().setId(leagueId);

        List<WeekScore> mockWeekScores1 = List.of(w1, w2);
        List<WeekScore> mockWeekScores2 = List.of(w3);

        when(repo.findWeekScoresByIdWeekNumAndLeagueId(1, leagueId)).thenReturn(mockWeekScores1);
        when(repo.findWeekScoresByIdWeekNumAndLeagueId(2, leagueId)).thenReturn(mockWeekScores2);

        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId(leagueId, 2);

        assertEquals(2, weekScores.size());

        List<WeekScore> realWeekScores1 = weekScores.get(0);
        List<WeekScore> realWeekScores2 = weekScores.get(1);

        assertEquals(2, realWeekScores1.size());
        assertEquals(1, realWeekScores2.size());

        realWeekScores1.forEach(weekScore -> assertEquals(
                "someLeagueId", weekScore.getId().getRosterUser().getLeague().getId()));
        realWeekScores2.forEach(weekScore -> assertEquals(
                "someLeagueId", weekScore.getId().getRosterUser().getLeague().getId()));
    }

    @Test
    void testObtainFewerWeekScoresWithLargeMaxWeek() {
        String leagueId = "someLeagueId";

        WeekScore w1 = createMockWeekScore();
        WeekScore w2 = createMockWeekScore();

        w1.getId().getRosterUser().getLeague().setId(leagueId);
        w2.getId().getRosterUser().getLeague().setId(leagueId);

        List<WeekScore> mockWeekScores = List.of(w1, w2);

        when(repo.findWeekScoresByIdWeekNumAndLeagueId(1, leagueId)).thenReturn(mockWeekScores);
        when(repo.findWeekScoresByIdWeekNumAndLeagueId(2, leagueId)).thenReturn(List.of());

        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId(leagueId, 10);

        assertEquals(1, weekScores.size());

        List<WeekScore> realWeekScores = weekScores.getFirst();
        assertEquals(2, realWeekScores.size());

        realWeekScores.forEach(weekScore -> assertEquals(
                "someLeagueId", weekScore.getId().getRosterUser().getLeague().getId()));
    }

    @Test
    void testObtainZeroWeekScoresWithNonPositiveMaxWeek() {
        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId("asd", 0);

        assertTrue(weekScores.isEmpty());
    }

    @Test
    void testObtainZeroWeekScoresWithMismatchLeagueId() {
        when(repo.findWeekScoresByIdWeekNumAndLeagueId(1, "asd")).thenReturn(List.of());

        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId("asd", 1);

        assertTrue(weekScores.isEmpty());
    }
}
