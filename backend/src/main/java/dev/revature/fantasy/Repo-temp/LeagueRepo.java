package dev.revature.fantasy.repo;

import org.springframework.stereotype.Repository;

import dev.revature.fantasy.model.League;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LeagueRepo extends JpaRepository<League, String>, LeagueRepoCustom {
    
    
}
