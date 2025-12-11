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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeekScoreServiceTest {
    @Mock
    WeekScoreRepo repo;

    @InjectMocks
    WeekScoreService service;

    @Test
    void testSavesAllWeekScores() {
        List<WeekScore> mockWeekScores = List.of(new WeekScore(null, 1.15));

        service.upsertWeekScores(mockWeekScores);

        verify(repo, times(1)).saveAll(mockWeekScores);
    }

    @Test
    void testUpdatesAllWeekScores() {
        String leagueId = "someLeagueId";

        List<WeekScore> mockWeekScores1 = List.of();
        List<WeekScore> mockWeekScores2 = List.of();

        when(repo.findWeekScoresByIdWeekNumAndLeagueId(1,leagueId)).thenReturn(mockWeekScores1);
        when(repo.findWeekScoresByIdWeekNumAndLeagueId(2,leagueId)).thenReturn(mockWeekScores2);

        List<List<WeekScore>> weekScores = service.findWeekScoresByLeagueId(leagueId,2);

        assertEquals(2,weekScores.size());

        List<WeekScore> realWeekScores1=weekScores.get(0);
        List<WeekScore> realWeekScores2=weekScores.get(1);

        assertEquals(2,realWeekScores1.size());
        assertEquals(1,realWeekScores2.size());
    }
}
