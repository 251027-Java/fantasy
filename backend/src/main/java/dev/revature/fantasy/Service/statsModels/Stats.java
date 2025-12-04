package dev.revature.fantasy.service.statsModels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.revature.fantasy.dto.LeagueStatsDto;
import dev.revature.fantasy.dto.ScoreDto;
import dev.revature.fantasy.dto.StatDto;
import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.model.WeekScore;


public class Stats {
    // potentially through helper classes. 
    private static final double MEDIAN_SCALE_FACTOR = 10; 

    private static final double LUCK_CUTOFF = 6;

    private static double calculateLuckAdjustment(double medianOffset, double luckCutoff, double scaleFactor) {
        // Check the cutoff condition once
        boolean withinCutoff = Math.abs(medianOffset) <= luckCutoff;

        double innerValue;
        // TODO: fix so that smallest amount that matters is a single pass yard (.04)
        if (!withinCutoff) {
            innerValue = 0;
        } else {
            if (medianOffset < 0) {
                innerValue = -1 / Math.sqrt(-1 * medianOffset);
            } else if (medianOffset > 0) {
                innerValue = 1 / Math.sqrt(medianOffset);
            } else { // ties with median (exceedingly rare)
                // counts as a loss so act as if it's 1 / sqrt(.04)
                // (the smallest amount it's possible to lose by that isnt a tie)
                innerValue = -1 / Math.sqrt(.02);
            }
        }

        return scaleFactor * innerValue;
    }


    // method to compute median luck score for a week (probably)
    public static MedianLuck computeMedianLuckScore(List<RosterUser> rosterUsers, List<List<WeekScore>> allScores) {
        Map<Long, List<Double>> medianLuckScoresByWeek = new HashMap<>();
        // init the map with empty lists
        for (RosterUser user : rosterUsers) {
            medianLuckScoresByWeek.put(user.getRosterUserId(), new ArrayList<>());
        }

        Map<Long, Double> totalMedianLuckScores = new HashMap<>();
        int numWeeks = allScores.size();


        for (int week = 0; week < allScores.size(); week++) {
            List<WeekScore> weekScores = allScores.get(week);

            // Extract raw scores
            List<Double> scores = weekScores.stream()
                    .map(WeekScore::getScore)
                    .sorted()
                    .toList();

            if (scores.isEmpty()) continue;

            // ----- 1. Compute Median -----
            double median;
            int n = scores.size();
            if (n % 2 == 1) {
                median = scores.get(n / 2);
            } else {
                median = (scores.get(n / 2 - 1) + scores.get(n / 2)) / 2.0;
            }

            // ----- 2. For each player, compute median offset & luck adjustment -----
            for (WeekScore ws : weekScores) {

                double playerScore = ws.getScore();
                double medianOffset = playerScore - median;
                double luckScore = calculateLuckAdjustment(medianOffset, LUCK_CUTOFF, MEDIAN_SCALE_FACTOR);
                long rosterUserId = ws.getId().getRosterUserId();
                medianLuckScoresByWeek.get(ws.getId().getRosterUserId()).add(luckScore);
                double currTotalLuckScore = totalMedianLuckScores.getOrDefault(rosterUserId, 0.0);
                totalMedianLuckScores.put(rosterUserId, currTotalLuckScore + luckScore);
            
            }
        }

        // average totalMedianLuckScores by numWeeks
        for (Long key : totalMedianLuckScores.keySet()) {
            totalMedianLuckScores.put(key, totalMedianLuckScores.get(key) / numWeeks);
        }

        return new MedianLuck(medianLuckScoresByWeek, totalMedianLuckScores);
}

    /**
     * Method to compute all play luck score. Requires that
     * @param rosterUsers the list of roster users
     * @param allScores a list of week scores, ie index 0 is all scores for week 1. 
     * The rosterUserIds should match with rosterUsers' rosterUserIds.
     */
    public static Map<Long, AllPlayData> computeAllPlayLuckScore(List<RosterUser> rosterUsers, List<List<WeekScore>> allScores) {
        // Map rosterUserId → all-play wins, losses
        Map<Long, Integer> allPlayWins = new HashMap<>();
        Map<Long, Integer> allPlayLosses = new HashMap<>();
        Map<Long, Integer> allPlayTies = new HashMap<>();
        Map<Long, RosterUser> idToRosterUser = new HashMap<>();

        Map<Long, AllPlayData> allPlayData = new HashMap<>();

        List<Long> rosterUserIds = rosterUsers.stream().map(RosterUser::getRosterUserId).toList();

        // Initialize counters
        for (RosterUser rosterUser : rosterUsers) {
            Long id = rosterUser.getRosterUserId();

            allPlayWins.put(id, 0);
            allPlayLosses.put(id, 0);
            allPlayTies.put(id, 0);
            idToRosterUser.put(id, rosterUser);
        }
    
        for (int week = 0; week < allScores.size(); week++) {

            // Get all WeekScores for this week for all requested roster user ids
            List<WeekScore> currWeekScores = allScores.get(week);

            // Compare each score with every other score
            for (WeekScore s1 : currWeekScores) {
                for (WeekScore s2 : currWeekScores) {
                    if (s1.equals(s2)) continue; // skip self

                    if (s1.getScore() > s2.getScore()) {
                        allPlayWins.merge(s1.getId().getRosterUserId(), 1, Integer::sum);
                    } else if (s1.getScore() < s2.getScore()) {
                        allPlayLosses.merge(s1.getId().getRosterUserId(), 1, Integer::sum);
                    } else {
                        allPlayTies.merge(s1.getId().getRosterUserId(), 1, Integer::sum);
                    }
                }
            }
        }

        // Now compute final results
        for (Long id : rosterUserIds) {
            int wins = allPlayWins.get(id);
            int losses = allPlayLosses.get(id);
            int ties = allPlayTies.get(id);

            double allPlayPct =
                    (wins + losses + ties) == 0 ? 0 
                    : (double) wins / (wins + losses + ties);

            RosterUser rosterUser = idToRosterUser.get(id);
            int actualWins = rosterUser.getWins();
            int actualLosses = rosterUser.getLosses();
            int actualTies = rosterUser.getTies();

            double actualPct =
                    (actualWins + actualLosses + actualTies) == 0 ? 0 
                    : (double) actualWins / (actualLosses + actualWins + actualTies);

            double luckScore = (actualPct - allPlayPct) * 100;

            AllPlayData userAllPlayData = new AllPlayData(allPlayPct, luckScore, wins, losses, ties, actualWins, actualLosses, actualTies);
            allPlayData.put(id, userAllPlayData);
        }
        return allPlayData;
        
    }


    // method to compute total luck score
    public static LuckData computeTotalLuckScore(List<RosterUser> rosterUsers, List<List<WeekScore>> allScores) {
        var allPlayLuckScores = computeAllPlayLuckScore(rosterUsers, allScores);
        var medianLuckScores = computeMedianLuckScore(rosterUsers, allScores);
        return new LuckData(medianLuckScores, allPlayLuckScores);
    }

    public static LeagueStatsDto luckDataToDto(LuckData luckData, Map<Long, String> rosterUserIdToName) {
        var allPlayLuckScores = luckData.getAllPlayLuck();
        var medianLuckScores = luckData.getMedianLuck();
        var totalMedianLuckScores = medianLuckScores.getTotalMedianLuckScores();

        List<StatDto> stats = new ArrayList<>();
        for (Long id : allPlayLuckScores.keySet()) {
            // create the statDto for each player
            AllPlayData allPlayData = allPlayLuckScores.get(id);
            double allPlayScore = allPlayData.getAllPlayLuckScore();
            int wins = allPlayData.getActualWins();
            int losses = allPlayData.getActualLosses();
            int ties = allPlayData.getActualTies();
            int allPlayWins = allPlayData.getAllPlayWins();
            int allPlayLosses = allPlayData.getAllPlayLosses();
            int allPlayTies = allPlayData.getAllPlayTies();
            double medLuckScore = totalMedianLuckScores.get(id);
            double totalLuckScore = medLuckScore + allPlayScore;

            ScoreDto scoreDto = new ScoreDto(
                totalLuckScore,
                medLuckScore,
                allPlayScore,
                allPlayWins,
                allPlayLosses,
                allPlayTies,
                wins,
                losses,
                ties
            );

            StatDto statDto = new StatDto(rosterUserIdToName.get(id), scoreDto);
            stats.add(statDto);

        }
        
        return new LeagueStatsDto(stats.toArray(new StatDto[stats.size()]));
    }

}
