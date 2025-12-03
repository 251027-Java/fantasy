package dev.revature.fantasy.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.revature.fantasy.model.RosterUser;

public interface RosterUserRepo extends JpaRepository<RosterUser, Long> {
    public Optional<RosterUser> findByUserIdAndLeagueId(String userId, String leagueId);
    public Optional<RosterUser> findByRosterIdAndLeagueId(Integer rosterId, String leagueId);
    
} 
