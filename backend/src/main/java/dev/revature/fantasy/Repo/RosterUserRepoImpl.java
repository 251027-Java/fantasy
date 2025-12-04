package dev.revature.fantasy.repo;

import dev.revature.fantasy.model.RosterUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RosterUserRepoImpl is a custom implementation of the RosterUserRepo interface.
 * It provides functionality for batch upserting RosterUser entities into the database.
 */
@Repository
public class RosterUserRepoImpl implements RosterUserRepoCustom {
    
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<RosterUser> rosterUserRowMapper;

    public RosterUserRepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rosterUserRowMapper = new RosterUserRowMapper();
    }

    @Override
    @Transactional
    public List<RosterUser> batchUpsert(List<RosterUser> rosterUsers) {
        
        // upsert native SQL
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
            """;
        
        List<Object[]> batchArgs = rosterUsers.stream()
            .map(user -> new Object[] {
                user.getRosterId(),
                user.getUserId(), 
                user.getLeagueId(),
                
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

        // perform query to get updated entities with database generated keys
        String identifiers = rosterUsers.stream()
            .map(u -> "(%d, '%s')".formatted(u.getRosterId(), u.getLeagueId()))
            .collect(Collectors.joining(","));

        final String selectSql = String.format("""
            SELECT roster_user_id, roster_id, user_id_num, league_id, wins, ties, losses,
                   fpts_decimal, fpts_against_decimal, fpts_against, fpts 
            FROM roster_user
            WHERE (roster_id, league_id) IN (%s)
            """, identifiers);

        List<RosterUser> updatedEntities = jdbcTemplate.query(selectSql, rosterUserRowMapper);

        return updatedEntities;
    }

    
    private static class RosterUserRowMapper implements RowMapper<RosterUser> {
        @Override
        public RosterUser mapRow(ResultSet rs, int rowNum) throws SQLException {
            RosterUser user = new RosterUser();
            
            // 1. Auto-Generated Primary Key
            user.setRosterUserId(rs.getLong("roster_user_id"));
            
            // 2. Business Keys
            user.setRosterId(rs.getInt("roster_id"));
            user.setLeagueId(rs.getString("league_id"));
            user.setUserId(rs.getString("user_id_num"));
            
            // 3. Stat Fields
            user.setWins(rs.getInt("wins"));
            user.setTies(rs.getInt("ties"));
            user.setLosses(rs.getInt("losses"));
            user.setFptsDecimal(rs.getInt("fpts_decimal"));
            user.setFptsAgainstDecimal(rs.getInt("fpts_against_decimal"));
            user.setFptsAgainst(rs.getInt("fpts_against"));
            user.setFpts(rs.getInt("fpts"));
            
            return user;
        }
    }

}