package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.repository.WeekScoreRepo;
import dev.revature.fantasy.sleeperrequest.ResponseFormatter;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperMatchupResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class WeekScoreServiceTest {
    @Mock
    WeekScoreRepo repo;

    @Mock
    ResponseFormatter responseFormatter;

    @Mock
    DatabaseFormatterService databaseFormatterService;

    @InjectMocks
    WeekScoreService service;

    @Test
    void upsertWeekScores_saveWeekScores_usesRepositoryToSave() {
        List<WeekScore> mockWeekScores = List.of();

        service.upsertWeekScores(mockWeekScores);

        verify(repo, times(1)).saveAll(mockWeekScores);
    }

    @Test
    void findWeekScoresByLeagueId_validLeagueIdAndUpToMaxWeek_returnsAllFoundWeekScores() {
        List<WeekScore> mockWeekScores1 = List.of(new WeekScore(), new WeekScore());
        List<WeekScore> mockWeekScores2 = List.of(new WeekScore());

        String leagueId = "someLeagueId";
        when(repo.findWeekScoresByIdWeekNumAndLeagueId(1, leagueId)).thenReturn(mockWeekScores1);
        when(repo.findWeekScoresByIdWeekNumAndLeagueId(2, leagueId)).thenReturn(mockWeekScores2);

        int maxWeek = 2;

        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId(leagueId, maxWeek);

        assertEquals(2, weekScores.size());

        List<WeekScore> realWeekScores1 = weekScores.get(0);
        List<WeekScore> realWeekScores2 = weekScores.get(1);

        assertEquals(mockWeekScores1, realWeekScores1);
        assertEquals(mockWeekScores2, realWeekScores2);
    }

    @Test
    void findWeekScoresByLeagueId_ValidLeagueIdAndLargeMaxWeek_returnsLessThanMaxWeekResults() {
        List<WeekScore> mockWeekScores1 = List.of(new WeekScore(), new WeekScore());

        String leagueId = "someLeagueId";
        when(repo.findWeekScoresByIdWeekNumAndLeagueId(1, leagueId)).thenReturn(mockWeekScores1);
        when(repo.findWeekScoresByIdWeekNumAndLeagueId(2, leagueId)).thenReturn(List.of());

        int maxWeek = 2;

        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId(leagueId, maxWeek);

        assertEquals(1, weekScores.size());

        List<WeekScore> realWeekScores1 = weekScores.getFirst();
        assertEquals(mockWeekScores1, realWeekScores1);
    }

    @Test
    void findWeekScoresByLeagueId_nonPositiveMaxWeek_returnsEmptyList() {
        String id = "asd";
        int maxWeek = 0;

        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId(id, maxWeek);

        assertTrue(weekScores.isEmpty());
    }

    @Test
    void findWeekScoresByLeagueId_nonExistantLeagueId_returnsEmptyList() {
        int maxWeek = 1;
        String id = "asd";
        when(repo.findWeekScoresByIdWeekNumAndLeagueId(1, id)).thenReturn(List.of());

        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId(id, maxWeek);

        assertTrue(weekScores.isEmpty());
    }

    @Test
    void concurrentGetWeekScores_getAllWeekScoresForLeague_returnsTheWeekScores() {
        String leagueId = "asd";

        SleeperMatchupResponse m1 = new SleeperMatchupResponse();
        SleeperMatchupResponse m2 = new SleeperMatchupResponse();
        SleeperMatchupResponse m3 = new SleeperMatchupResponse();

        List<SleeperMatchupResponse> mockMatchup1 = List.of(m1, m2);
        List<SleeperMatchupResponse> mockMatchup2 = List.of(m3);

        int week1 = 1;
        int week2 = 2;

        when(responseFormatter.nonBlockGetMatchupsFromLeagueIdAndWeek(leagueId, week1))
                .thenReturn(Mono.just(mockMatchup1));
        when(responseFormatter.nonBlockGetMatchupsFromLeagueIdAndWeek(leagueId, week2))
                .thenReturn(Mono.just(mockMatchup2));

        when(databaseFormatterService.formatMatchups(mockMatchup1, leagueId, week1))
                .thenReturn(List.of(new WeekScore(), new WeekScore()));
        when(databaseFormatterService.formatMatchups(mockMatchup2, leagueId, week2))
                .thenReturn(List.of(new WeekScore()));

        int numWeeksFound = 0;
        int numWeeksToCompute = 2;

        List<WeekScore> weekScores = service.concurrentGetWeekScores(leagueId, numWeeksFound, numWeeksToCompute);

        assertEquals(3, weekScores.size());
    }
}
