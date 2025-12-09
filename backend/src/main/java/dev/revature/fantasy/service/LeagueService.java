package dev.revature.fantasy.service;

import dev.revature.fantasy.model.League;
import dev.revature.fantasy.repository.LeagueRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeagueService {
    private final LeagueRepo leagueRepo;

    public LeagueService(LeagueRepo leagueRepo) {
        this.leagueRepo = leagueRepo;
    }

    @Transactional
    public List<League> idempotentSave(List<League> leagues) {
        leagueRepo.saveAll(leagues);
        return leagues;
    }

    /**
     * Returns the size of the league, or -1 if not found in database
     * @param leagueId the league id
     * @return the number of players in the league
     */
    public int getSizeOfLeague(String leagueId) {
        Optional<League> league = this.leagueRepo.findById(leagueId);
        return league.isPresent() ? league.get().getNumRosters() : -1;
    }

    public League getReference(String id) {
        return leagueRepo.getReferenceById(id);
    }
}
