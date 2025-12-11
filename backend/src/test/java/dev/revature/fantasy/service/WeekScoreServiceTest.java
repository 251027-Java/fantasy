package dev.revature.fantasy.service;

import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.repository.WeekScoreRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeekScoreServiceTest {
    @Mock
    WeekScoreRepo repo;

    @InjectMocks
    WeekScoreService service;

    @Test
    void saveWeekScores_usesRepositoryToSave() {
        List<WeekScore> mockWeekScores = List.of();

        service.upsertWeekScores(mockWeekScores);

        verify(repo, times(1)).saveAll(mockWeekScores);
    }

    @Test
    void obtainWeekScoresWithValidLeagueIdAndUpToMaxWeek_returnsAllFoundWeekScores() {
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
    void obtainWeekScoresWithValidLeagueIdAndLargeMaxWeek_returnsLessThanMaxWeekResults() {
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
    void tryObtainWeekScoresWithNonPositiveMaxWeek_returnsEmptyList() {
        String id = "asd";
        int maxWeek = 0;

        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId(id, maxWeek);

        assertTrue(weekScores.isEmpty());
    }

    @Test
    void tryObtainWeekScoresWithNonExistantLeagueId() {
        int maxWeek = 1;
        String id = "asd";
        when(repo.findWeekScoresByIdWeekNumAndLeagueId(1, id)).thenReturn(List.of());

        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId(id, maxWeek);

        assertTrue(weekScores.isEmpty());
    }
}
