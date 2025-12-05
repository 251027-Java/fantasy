package dev.revature.fantasy.service;

import dev.revature.fantasy.model.League;
import dev.revature.fantasy.repository.LeagueRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeagueService {
    private final LeagueRepo leagueRepo;

    public LeagueService(LeagueRepo leagueRepo) {
        this.leagueRepo = leagueRepo;
    }

    @Transactional
    public List<League> idempotentSave(List<League> leagues) {
        this.leagueRepo.batchIdempotentSave(leagues);
        return leagues;
    }
}
