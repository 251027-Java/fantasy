package dev.revature.fantasy.service.statsmodel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class MedianLuck {
    /**
     * Map of rosterUserId → median luck scores for each week
     */
    private Map<Long, List<Double>> medianLuckScoresByWeek;

    /**
     * Map of rosterUserId → total median luck scores
     */
    private Map<Long, Double> totalMedianLuckScores;
}
