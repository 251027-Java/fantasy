package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.revature.fantasy.dto.LeagueStatsDto;
import dev.revature.fantasy.dto.StatDto;
import dev.revature.fantasy.dto.WeeklyMedianLuckDto;
import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.model.WeekScoreId;
import dev.revature.fantasy.service.statsmodel.AllPlayData;
import dev.revature.fantasy.service.statsmodel.LuckData;
import dev.revature.fantasy.service.statsmodel.MedianLuck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class StatsComputationServiceTest {
    static final double EPSILON = 1e-3;

    StatsComputationService service;

    @BeforeEach
    void setUp() {
        service = new StatsComputationService();
    }

    WeekScore createWeekScore(RosterUser rosterUser, double score) {
        WeekScoreId weekScoreId = new WeekScoreId();
        weekScoreId.setRosterUser(rosterUser);

        WeekScore weekScore = new WeekScore();
        weekScore.setId(weekScoreId);
        weekScore.setScore(score);

        return weekScore;
    }

    @Test
    void computeStats_singleWeekWithThreeUsers_returnsTheStats() {
        RosterUser r1 = new RosterUser();
        r1.setId(2L);
        r1.setWins(2);
        r1.setLosses(2);
        r1.setTies(0);

        RosterUser r2 = new RosterUser();
        r2.setId(3L);
        r2.setWins(3);
        r2.setLosses(1);
        r2.setTies(0);

        RosterUser r3 = new RosterUser();
        r3.setId(4L);
        r3.setWins(1);
        r3.setLosses(3);
        r3.setTies(0);

        WeekScore w1 = createWeekScore(r1, 22);
        WeekScore w2 = createWeekScore(r2, 6);
        WeekScore w3 = createWeekScore(r3, 2);

        List<List<WeekScore>> weekScores = List.of(List.of(w1, w2, w3));
        List<RosterUser> rosterUsers = List.of(r1, r2, r3);
        // end of arrange

        LuckData realLuckData = service.computeStats(rosterUsers, weekScores);

        Map<Long, Double> totalMedianLuckScores = realLuckData.getMedianLuck().getTotalMedianLuckScores();
        double defaultMedianValue = -1 / Math.sqrt(.02) * 10;

        assertEquals(0, totalMedianLuckScores.get(2L));
        assertEquals(defaultMedianValue, totalMedianLuckScores.get(3L), EPSILON);
        assertEquals(-5, totalMedianLuckScores.get(4L));

        Map<Long, AllPlayData> allPlayLuck = realLuckData.getAllPlayLuck();

        assertEquals(1, allPlayLuck.get(2L).getAllPlayPercentage(), EPSILON);
        assertEquals(.5, allPlayLuck.get(3L).getAllPlayPercentage(), EPSILON);
        assertEquals(0, allPlayLuck.get(4L).getAllPlayPercentage(), EPSILON);
    }

    @Test
    void toDto_validLuckDataAndMapping_returnsStatsInDtoForm() {
        long rosterUserId = 1L;

        Map<Long, Double> totalMedianLuckScores = Map.of(rosterUserId, 5.5);
        Map<Long, List<Double>> medianLuckScoresByWeek = Map.of(rosterUserId, List.of(2.0));
        MedianLuck medianLuck = new MedianLuck(medianLuckScoresByWeek, totalMedianLuckScores);

        Map<Long, AllPlayData> allPlayDataMap = Map.of(rosterUserId, new AllPlayData());

        LuckData luckData = new LuckData(medianLuck, allPlayDataMap);

        Map<Long, String> userNameMapping = Map.of(rosterUserId, "some username");
        // end of arrange

        LeagueStatsDto dto = service.toDto(luckData, userNameMapping);

        StatDto[] stats = dto.getStats();
        assertEquals(1, stats.length);
        assertEquals("some username", stats[0].getUserName());

        WeeklyMedianLuckDto[] weeklyMedianLuck = dto.getWeeklyMedianLuck();
        assertEquals(1, weeklyMedianLuck.length);
        assertEquals("some username", weeklyMedianLuck[0].getUserName());
    }
}
