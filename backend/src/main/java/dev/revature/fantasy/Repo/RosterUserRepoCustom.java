package dev.revature.fantasy.repo;

import java.util.List;

import dev.revature.fantasy.model.RosterUser;

public interface RosterUserRepoCustom {
    public List<RosterUser> batchUpsert(List<RosterUser> rosterUsers);
}
