package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.model.WeekScoreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeekScoreRepo extends JpaRepository<WeekScore, WeekScoreId> {
    public List<WeekScore> findWeekScoresByLeagueId(String leagueId);

    public List<WeekScore> findWeekScoresByIdWeekNumAndLeagueId(int week, String leagueId);
}
