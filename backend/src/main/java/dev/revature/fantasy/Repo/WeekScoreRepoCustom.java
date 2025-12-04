package dev.revature.fantasy.repo;

import java.util.List;
import dev.revature.fantasy.model.WeekScore;

public interface WeekScoreRepoCustom {
    /**
     * Performs a batch upsert operation on a list of WeekScore entities.
     * Uses native SQL to ON CONFLICT (Primary Key) DO UPDATE
     * * @param weekScores The list of scores to save/update.
     * @return An array of integers containing the update counts for each item in the batch.
     */
    int[] batchUpsert(List<WeekScore> weekScores);
}