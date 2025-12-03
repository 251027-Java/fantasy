package dev.revature.fantasy.repo;

import dev.revature.fantasy.model.RosterUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// You would need to ensure RosterUserRepo extends RosterUserRepoCustom
// and RosterUserRepoCustom defines the batchIdempotentSave method.
@Repository
public class RosterUserRepoImpl implements RosterUserRepoCustom {
    
    private final JdbcTemplate jdbcTemplate;

    public RosterUserRepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public List<RosterUser> batchUpsert(List<RosterUser> rosterUsers) {
        
        // --- NATIVE SQL INSERT ... ON CONFLICT (UPSERT) ---
        // Idempotency relies on the unique constraint of (roster_id, league_id).
        // If a row exists, we update all the statistic columns.
        
        final String sql = """
            INSERT INTO roster_user (roster_id, user_id_num, league_id, wins, ties, losses, 
                                     fpts_decimal, fpts_against_decimal, fpts_against, fpts)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (roster_id, league_id) 
            DO UPDATE SET 
                user_id_num = EXCLUDED.user_id_num,
                wins = EXCLUDED.wins, 
                ties = EXCLUDED.ties, 
                losses = EXCLUDED.losses, 
                fpts_decimal = EXCLUDED.fpts_decimal, 
                fpts_against_decimal = EXCLUDED.fpts_against_decimal, 
                fpts_against = EXCLUDED.fpts_against, 
                fpts = EXCLUDED.fpts
            RETURNING *
            """;
        
        // 1. Prepare the batch arguments
        List<Object[]> batchArgs = rosterUsers.stream()
            .map(user -> new Object[] {
                // PK/Conflict Fields
                user.getRosterId(),
                user.getUserId(), 
                user.getLeagueId(),
                
                // Data Fields (Stats)
                user.getWins(),
                user.getTies(),
                user.getLosses(),
                user.getFptsDecimal(),
                user.getFptsAgainstDecimal(),
                user.getFptsAgainst(),
                user.getFpts()
            })
            .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);

        //TODO: make sure to return the updated RosterUserIds
        return rosterUsers; 
    }
}