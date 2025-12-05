package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.League;

import java.util.List;

public interface LeagueRepoCustom {
    public int[] batchIdempotentSave(List<League> leagues);
}
