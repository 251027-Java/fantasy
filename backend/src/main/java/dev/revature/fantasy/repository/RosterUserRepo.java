package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.RosterUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RosterUserRepo extends JpaRepository<RosterUser, Long>, RosterUserRepoCustom {
    public Optional<RosterUser> findByUserIdAndLeagueId(String userId, String leagueId);

    public Optional<RosterUser> findByRosterIdAndLeagueId(Integer rosterId, String leagueId);
}
