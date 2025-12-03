package dev.revature.fantasy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

import dev.revature.fantasy.dto.*;
import dev.revature.fantasy.logger.GlobalLogger;
import dev.revature.fantasy.model.*;
import dev.revature.fantasy.sleeperRequest.sleeperResponseModels.*;

@Service
public class DatabaseFormatterService {
    private final RosterUserService rosterUserService;
    public DatabaseFormatterService(RosterUserService rosterUserService) {
        this.rosterUserService = rosterUserService;
    }

    public static List<League> formatLeagueInfo(List<SleeperLeagueResponse> sleeperLeagues) { 
        List<League> leagues = new ArrayList<>();

        for (SleeperLeagueResponse league : sleeperLeagues) {
            leagues.add(new League(league.getLeagueId(), league.getNumTeams(), league.getName(), league.getSeasonYear()));
        }
        return leagues;
    }

    public static List<User> formatUsers(List<SleeperUserResponse> sleeperUsers) { 
        List<User> users = new ArrayList<>(); 
        for (SleeperUserResponse user : sleeperUsers) {
            users.add(new User(user.getUserId(), user.getDisplayName()));
        }
        return users;
    }

    public static List<RosterUserDto> formatRosterUsers(
        List<SleeperRosterUserResponse> sleeperRosterUsers
    ) { 
        List<RosterUserDto> rosterUsers = new ArrayList<>(); 
        for (SleeperRosterUserResponse sleeperRosterUser : sleeperRosterUsers) {
            // upsert the rosterUsers to the database
            UserSettings rosterUserSettings = sleeperRosterUser.getSettings();
            rosterUsers.add(
                new RosterUserDto(
                    sleeperRosterUser.getRosterId(),
                    sleeperRosterUser.getUserId(), 
                    sleeperRosterUser.getLeagueId(),
                    rosterUserSettings.getWins(),
                    rosterUserSettings.getTies(),
                    rosterUserSettings.getLosses(),
                    rosterUserSettings.getFptsDecimal(),
                    rosterUserSettings.getFptsAgainstDecimal(),
                    rosterUserSettings.getFptsAgainst(),
                    rosterUserSettings.getFpts()
                )
            );
        }
        return rosterUsers;
    } 

    public List<WeekScore> formatMatchups(List<SleeperMatchupResponse> sleeperMatchups, String leagueId, int week) {
        List<WeekScore> weekScores = new ArrayList<>();
        for (SleeperMatchupResponse matchup : sleeperMatchups) {
            var rosterUser = this.rosterUserService
            .getRosterUserByRosterIdAndLeagueId(matchup.getRosterId(), leagueId);
            if (rosterUser.isEmpty()) { // should never be empty but 
                GlobalLogger.error(
                    "roster is empty for rosterId: " + matchup.getRosterId() + " and leagueId: " + leagueId);
                continue;
            }
            WeekScoreId weekScoreId = new WeekScoreId(rosterUser.get().getRosterUserId(), week);
            WeekScore weekScore = new WeekScore(weekScoreId, matchup.getPoints(), leagueId);
            weekScores.add(weekScore);
        }
        return weekScores;
    }
}
