package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.WeekScore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class WeekScoreRepoImpl implements WeekScoreRepoCustom {

    private final JdbcTemplate jdbcTemplate;

    public WeekScoreRepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public int[] batchUpsert(List<WeekScore> weekScores) {

        final String sql =
                """
            INSERT INTO week_score (roster_user_id, week_num, score, league_id)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (roster_user_id, week_num)
            DO UPDATE SET
                score = EXCLUDED.score,
                league_id = EXCLUDED.league_id
            """;

        List<Object[]> batchArgs = weekScores.stream()
                .map(score -> new Object[] {
                    score.getId().getRosterUserId(), score.getId().getWeekNum(), score.getScore(), score.getLeagueId()
                })
                .toList();

        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
