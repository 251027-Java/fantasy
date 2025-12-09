package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.model.WeekScoreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeekScoreRepo extends JpaRepository<WeekScore, WeekScoreId> {
    @NativeQuery(
            value =
                    """
        SELECT week_score.*
        FROM
            week_score
            JOIN roster_user ON roster_user.id = week_score.roster_user_id
        WHERE
            roster_user.league_id = :league_id
            AND week_score.week_num = :week
        """)
    public List<WeekScore> findWeekScoresByIdWeekNumAndLeagueId(
            @Param("week") int week, @Param("league_id") String leagueId);
}
