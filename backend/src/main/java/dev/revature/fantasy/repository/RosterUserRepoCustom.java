package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.RosterUser;

import java.util.List;

public interface RosterUserRepoCustom {
    public List<RosterUser> batchUpsert(List<RosterUser> rosterUsers);
}
