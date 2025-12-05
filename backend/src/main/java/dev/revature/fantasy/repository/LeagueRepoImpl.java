package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.League;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LeagueRepoImpl implements LeagueRepoCustom {
    private final JdbcTemplate jdbcTemplate;

    // Inject JdbcTemplate into the custom implementation class
    public LeagueRepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int[] batchIdempotentSave(List<League> leagues) {
        final String sql =
                """
            INSERT INTO league (league_id, league_name, num_rosters, season)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (league_id) DO NOTHING
            """;

        // 1. Prepare the arguments
        List<Object[]> batchArgs = leagues.stream()
                .map(league -> new Object[] {
                    league.getLeagueId(), league.getLeagueName(), league.getNumRosters(), league.getSeasonYear()
                })
                .toList();

        // 2. Execute the single batch update using JdbcTemplate
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
