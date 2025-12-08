package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeagueRepo extends JpaRepository<League, String> {}
