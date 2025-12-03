package dev.revature.fantasy.repo;

import java.util.List;

import dev.revature.fantasy.model.League;

public interface LeagueRepoCustom {
    public int[] batchIdempotentSave(List<League> leagues);
}
