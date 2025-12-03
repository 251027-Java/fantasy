package dev.revature.fantasy.repo;

import java.util.List;
import dev.revature.fantasy.model.WeekScore;

public interface WeekScoreRepoCustom {
    /**
     * Performs a batch save operation on a list of WeekScore entities.
     * Uses native SQL INSERT ... ON CONFLICT (Primary Key) DO UPDATE/NOTHING 
     * to ensure the operation is idempotent and performed in a single database communication.
     * * @param weekScores The list of scores to save/update.
     * @return An array of integers containing the update counts for each item in the batch.
     */
    int[] batchUpsert(List<WeekScore> weekScores);
}