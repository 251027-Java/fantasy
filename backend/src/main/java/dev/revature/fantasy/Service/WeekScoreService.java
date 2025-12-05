package dev.revature.fantasy.service;

import org.springframework.stereotype.Service;

import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.repo.WeekScoreRepo;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeekScoreService {
    private final WeekScoreRepo repo;

    public WeekScoreService(WeekScoreRepo repo) {
        this.repo = repo;
    }

    public List<List<WeekScore>> findWeekScoresByLeagueId(String leagueId, int maxWeek) {
        List<List<WeekScore>> allWeekScores = new ArrayList<>();
        for (int week = 1; week < maxWeek; week++) {
            allWeekScores.add(this.repo.findWeekScoresByIdWeekNumAndLeagueId(week, leagueId));
        }
        return allWeekScores;
    }

    public void upsertWeekScores(List<WeekScore> weekScores) {
        this.repo.batchUpsert(weekScores);
    }

}
