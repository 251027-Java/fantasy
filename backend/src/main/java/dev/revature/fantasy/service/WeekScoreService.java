package dev.revature.fantasy.service;

import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.repository.WeekScoreRepo;
import dev.revature.fantasy.sleeperrequest.ResponseFormatter;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperMatchupResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class WeekScoreService {
    private final WeekScoreRepo repo;
    private static final int MAX_CONCURRENCY = Runtime.getRuntime().availableProcessors();
    private final ResponseFormatter responseFormatter;
    private final DatabaseFormatterService databaseFormatterService;

    public WeekScoreService(
            WeekScoreRepo repo,
            ResponseFormatter responseFormatter,
            DatabaseFormatterService databaseFormatterService) {
        this.repo = repo;
        this.responseFormatter = responseFormatter;
        this.databaseFormatterService = databaseFormatterService;
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

    public List<WeekScore> concurrentGetWeekScores(String leagueId, int numWeeksFound, int numWeeksToCompute) {
        var monoScores = Flux.range(numWeeksFound + 1, numWeeksToCompute - numWeeksFound)
                .flatMap(
                        (Integer week) -> {
                            Mono<List<SleeperMatchupResponse>> matchupsMono =
                                    this.responseFormatter.nonBlockGetMatchupsFromLeagueIdAndWeek(leagueId, week);

                            return matchupsMono.flatMapIterable((List<SleeperMatchupResponse> matchups) ->
                                    this.databaseFormatterService.formatMatchups(matchups, leagueId, week));
                        },
                        MAX_CONCURRENCY)
                .collectList();

        List<WeekScore> scores = monoScores.block();

        return scores;
    }
}
