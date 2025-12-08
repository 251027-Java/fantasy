package dev.revature.fantasy.service;

import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.repository.WeekScoreRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeekScoreService {
    private final WeekScoreRepo repo;

    public WeekScoreService(WeekScoreRepo repo) {
        this.repo = repo;
    }

    /**
     * Gets all week scores for a given league up to a max week, inclusive.
     * @param leagueId the league id
     * @param maxWeek the max week to get week scores for
     * @return a list of lists of week scores, where each inner list is the week scores for a given week
     */
    public List<List<WeekScore>> findWeekScoresByLeagueId(String leagueId, int maxWeek) {
        List<List<WeekScore>> allWeekScores = new ArrayList<>();
        for (int week = 1; week <= maxWeek; week++) {
            var currWeekScores = this.repo.findWeekScoresByIdWeekNumAndLeagueId(week, leagueId);
            if (currWeekScores.size() == 0) { // no scores for this week so no need to look further
                return allWeekScores; // return what we have so far
            }
            allWeekScores.add(currWeekScores);
        }
        return allWeekScores;
    }

    /**
     * Upserts a list of week scores to database.
     * @param weekScores the week scores to upsert
     */
    public void upsertWeekScores(List<WeekScore> weekScores) {
        repo.saveAll(weekScores);
    }
}
