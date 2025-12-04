package dev.revature.fantasy.service;

import java.lang.classfile.ClassFile.Option;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import dev.revature.fantasy.dto.RosterUserDto;
import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.model.User;
import dev.revature.fantasy.repo.RosterUserRepo;
import dev.revature.fantasy.repo.UserRepo;

@Service
public class RosterUserService {
    private final RosterUserRepo repo;
    private final UserRepo userRepo;
    
    public RosterUserService(RosterUserRepo repo, UserRepo userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public Optional<RosterUser> getRosterUserByUserIdAndLeagueId(String userId, String leagueId) {
        return repo.findByUserIdAndLeagueId(userId, leagueId);
    }

    public Optional<RosterUser> getRosterUserByRosterIdAndLeagueId(Integer rosterId, String leagueId) {
        return repo.findByRosterIdAndLeagueId(rosterId, leagueId);
    }

    public List<RosterUser> upsertUsers(List<RosterUserDto> rosterUserDtos) {

        List<RosterUser> rosterUsers = rosterUserDtos.stream().map(
            (rosterUserDto) -> {
                RosterUser rosterUser = new RosterUser();
                rosterUser.setRosterId(rosterUserDto.rosterId());
                rosterUser.setUserId(rosterUserDto.userId());
                rosterUser.setLeagueId(rosterUserDto.leagueId());
                
                rosterUser.setWins(rosterUserDto.wins());
                rosterUser.setTies(rosterUserDto.ties());
                rosterUser.setLosses(rosterUserDto.losses());
                rosterUser.setFptsDecimal(rosterUserDto.fptsDecimal());
                rosterUser.setFptsAgainstDecimal(rosterUserDto.fptsAgainstDecimal());
                rosterUser.setFptsAgainst(rosterUserDto.fptsAgainst());
                rosterUser.setFpts(rosterUserDto.fpts());
                
                return rosterUser;
            }
        ).toList();
        return repo.batchUpsert(rosterUsers);
    }
    

    public Map<Long, String> getRosterUserIdToName(List<RosterUser> rosterUsers) {
        
        // 1. Extract all unique userIds (the join key) from the list of RosterUsers.
        Set<String> userIds = rosterUsers.stream()
            .map(RosterUser::getUserId)
            .collect(Collectors.toSet());

        List<User> users = userRepo.findAllById(userIds); 
        Map<String, String> userIdToDisplayName = users.stream()
            .collect(Collectors.toMap(User::getUserId, User::getDisplayName));

        Map<Long, String> rosterUserIdToName = new HashMap<>();

        for (RosterUser rosterUser : rosterUsers) {
            String userId = rosterUser.getUserId();
            String displayName = userIdToDisplayName.getOrDefault(userId, "Unknown User");
            rosterUserIdToName.put(rosterUser.getRosterUserId(), displayName);
        }
        
        return rosterUserIdToName;
    }
}
