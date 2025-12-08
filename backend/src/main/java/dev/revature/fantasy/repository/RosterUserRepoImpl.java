package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.RosterUser;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * RosterUserRepoImpl is a custom implementation of the RosterUserRepo interface.
 * It provides functionality for batch upserting RosterUser entities into the database.
 */
@Repository
public class RosterUserRepoImpl implements RosterUserRepoCustom {

    private final JdbcTemplate jdbcTemplate;

    public RosterUserRepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public List<RosterUser> batchUpsert(List<RosterUser> rosterUsers) {
        String sql =
                """
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
            RETURNING
                roster_user_id
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int[] ret = jdbcTemplate.batchUpdate(
                con -> con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS),
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        RosterUser rosterUser = rosterUsers.get(i);
                        ps.setInt(1, rosterUser.getRosterId());
                        ps.setString(2, rosterUser.getUserId());
                        ps.setString(3, rosterUser.getLeagueId());
                        ps.setInt(4, rosterUser.getWins());
                        ps.setInt(5, rosterUser.getTies());
                        ps.setInt(6, rosterUser.getLosses());
                        ps.setInt(7, rosterUser.getFptsDecimal());
                        ps.setInt(8, rosterUser.getFptsAgainstDecimal());
                        ps.setInt(9, rosterUser.getFptsAgainst());
                        ps.setInt(10, rosterUser.getFpts());
                    }

                    @Override
                    public int getBatchSize() {
                        return rosterUsers.size();
                    }
                },
                keyHolder);

        // update rosters with id
        var keys = keyHolder.getKeyList();

        for (int i = 0; i < rosterUsers.size(); i++) {
            var row = keys.get(i);
            var user = rosterUsers.get(i);
            user.setRosterUserId((Long) row.get("roster_user_id"));
        }

        return rosterUsers;
    }
}
