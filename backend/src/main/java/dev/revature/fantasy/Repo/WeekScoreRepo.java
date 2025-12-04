package dev.revature.fantasy.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.model.WeekScoreId;

@Repository
public interface WeekScoreRepo extends JpaRepository<WeekScore, WeekScoreId>, WeekScoreRepoCustom {
    public List<WeekScore> findWeekScoresByLeagueId(String leagueId);
    public List<WeekScore> findWeekScoresByIdWeekNumAndLeagueId(int week, String leagueId);
}
