package dev.revature.fantasy.repo;

import dev.revature.fantasy.model.WeekScore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Spring finds this class automatically because it implements WeekScoreRepoCustom 
// and follows the naming convention (WeekScoreRepo + Impl).
@Repository
public class WeekScoreRepoImpl implements WeekScoreRepoCustom {

    private final JdbcTemplate jdbcTemplate;

    // JdbcTemplate is automatically injected by Spring
    public WeekScoreRepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional // Ensure the entire batch runs as a single transaction
    public int[] batchUpsert(List<WeekScore> weekScores) {
        
        // --- NATIVE SQL INSERT ... ON CONFLICT (UPSERT) ---
        // The unique constraint used for conflict detection is the composite primary key:
        // (roster_user_id, week_num)
        final String sql = """
            INSERT INTO week_score (roster_user_id, week_num, score, league_id)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (roster_user_id, week_num) 
            DO UPDATE SET 
                score = EXCLUDED.score, 
                league_id = EXCLUDED.league_id
            """;
        
        // 1. Prepare the batch arguments from the list of WeekScore entities
        List<Object[]> batchArgs = weekScores.stream()
            .map(score -> new Object[] {
                // Primary Key Components (used for conflict detection)
                score.getId().getRosterUserId(),  // Placeholder 1: roster_user_id
                score.getId().getWeekNum(),       // Placeholder 2: week_num
                
                // Data Columns (updated if conflict occurs)
                score.getScore(),                 // Placeholder 3: score
                score.getLeagueId()               // Placeholder 4: league_id
            })
            .collect(Collectors.toList());

        // 2. Execute the batch command (single communication to the database)
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}