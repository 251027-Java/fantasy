package dev.revature.fantasy.service;

import dev.revature.fantasy.dto.RosterUserDto;
import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.model.User;
import dev.revature.fantasy.repository.LeagueRepo;
import dev.revature.fantasy.repository.RosterUserRepo;
import dev.revature.fantasy.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RosterUserService {
    private final RosterUserRepo repo;
    private final UserRepo userRepo;
    private final LeagueRepo leagueRepo;

    public RosterUserService(RosterUserRepo repo, UserRepo userRepo, LeagueRepo leagueRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.leagueRepo = leagueRepo;
    }

    public Optional<RosterUser> getRosterUserByUserIdAndLeagueId(String userId, String leagueId) {
        return repo.findByUserIdAndLeagueId(userId, leagueId);
    }

    public List<RosterUser> getAllRosterUsersByLeagueId(String leagueId) {
        return repo.findRosterUsersByLeagueId(leagueId);
    }

    public Optional<RosterUser> getRosterUserByRosterIdAndLeagueId(Integer rosterId, String leagueId) {
        return repo.findByRosterIdAndLeagueId(rosterId, leagueId);
    }

    public List<RosterUser> upsertUsers(List<RosterUserDto> rosterUserDtos) {

        List<RosterUser> rosterUsers = rosterUserDtos.stream()
                .map((rosterUserDto) -> {
                    RosterUser rosterUser = new RosterUser();
                    rosterUser.setRosterId(rosterUserDto.rosterId());
                    rosterUser.setUser(userRepo.getReferenceById(rosterUserDto.userId()));
                    rosterUser.setLeague(leagueRepo.getReferenceById(rosterUserDto.leagueId()));

                    rosterUser.setWins(rosterUserDto.wins());
                    rosterUser.setTies(rosterUserDto.ties());
                    rosterUser.setLosses(rosterUserDto.losses());
                    rosterUser.setFptsDecimal(rosterUserDto.fptsDecimal());
                    rosterUser.setFptsAgainstDecimal(rosterUserDto.fptsAgainstDecimal());
                    rosterUser.setFptsAgainst(rosterUserDto.fptsAgainst());
                    rosterUser.setFpts(rosterUserDto.fpts());

                    return rosterUser;
                })
                .toList();
        return repo.batchUpsert(rosterUsers);
    }

    public Map<Long, String> getRosterUserIdToName(List<RosterUser> rosterUsers) {

        // 1. Extract all unique userIds (the join key) from the list of RosterUsers.
        Set<String> userIds = rosterUsers.stream().map(e -> e.getUser().getId()).collect(Collectors.toSet());

        List<User> users = userRepo.findAllById(userIds);
        Map<String, String> userIdToDisplayName =
                users.stream().collect(Collectors.toMap(User::getId, User::getDisplayName));

        Map<Long, String> rosterUserIdToName = new HashMap<>();

        for (RosterUser rosterUser : rosterUsers) {
            String userId = rosterUser.getUser().getId();
            String displayName = userIdToDisplayName.getOrDefault(userId, "Unknown User");
            rosterUserIdToName.put(rosterUser.getId(), displayName);
        }

        return rosterUserIdToName;
    }
}
