package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.RosterUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RosterUserRepo extends JpaRepository<RosterUser, Long>, RosterUserRepoCustom {
    public Optional<RosterUser> findByUserIdAndLeagueId(String userId, String leagueId);

    public Optional<RosterUser> findByRosterIdAndLeagueId(Integer rosterId, String leagueId);

    public List<RosterUser> findRosterUsersByLeagueId(String leagueId);
}
