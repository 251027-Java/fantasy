package dev.revature.fantasy.Repo;

import org.springframework.stereotype.Repository;

import dev.revature.fantasy.Model.League;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LeagueRepo extends JpaRepository<League, String> {
    
}
