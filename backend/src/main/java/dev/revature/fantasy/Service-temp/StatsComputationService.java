package dev.revature.fantasy.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import dev.revature.fantasy.dto.LeagueStatsDto;
import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.model.WeekScore;
import dev.revature.fantasy.service.statsModels.LuckData;
import dev.revature.fantasy.service.statsModels.Stats;

@Service
public class StatsComputationService {
    
    public LuckData computeStats(List<RosterUser> rosterUsers, List<List<WeekScore>> allScores) {
        return Stats.computeTotalLuckScore(rosterUsers, allScores);
    }

    public LeagueStatsDto toDto(LuckData luckData, Map<Long, String> rosterUserIdToName) {
        return Stats.luckDataToDto(luckData, rosterUserIdToName);
    }
}
