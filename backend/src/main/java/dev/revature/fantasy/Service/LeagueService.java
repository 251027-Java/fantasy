package dev.revature.fantasy.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import dev.revature.fantasy.model.League;
import dev.revature.fantasy.repo.LeagueRepo;
import jakarta.transaction.Transactional;

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
