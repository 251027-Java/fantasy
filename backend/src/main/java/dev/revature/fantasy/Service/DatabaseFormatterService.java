package dev.revature.fantasy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

import dev.revature.fantasy.dto.*;
import dev.revature.fantasy.logger.GlobalLogger;
import dev.revature.fantasy.model.*;
import dev.revature.fantasy.sleeperRequest.sleeperResponseModels.*;

/**
 * Converts sleeper responses to database models. There is coupling with 
 * RosterUserService atm to get the user id in format matchups .
 */
@Service
public class DatabaseFormatterService {
    private final RosterUserService rosterUserService;
    public DatabaseFormatterService(RosterUserService rosterUserService) {
        this.rosterUserService = rosterUserService;
    }

    /**
     * Converts sleeper league responses to database league models
     * @param sleeperLeagues the sleeper league responses
     * @return the list of leagues to be inserted to the database
     */
    public static List<League> formatLeagueInfo(List<SleeperLeagueResponse> sleeperLeagues) { 
        List<League> leagues = new ArrayList<>();

        for (SleeperLeagueResponse league : sleeperLeagues) {
            leagues.add(new League(league.getLeagueId(), league.getNumTeams(), league.getName(), league.getSeasonYear()));
        }
        return leagues;
    }

    /**
     * Converts sleeper user responses to database user models
     * @param sleeperUsers the sleeper user responses
     * @return the list of users to be inserted to the database
     */
    public static List<User> formatUsers(List<SleeperUserResponse> sleeperUsers) { 
        List<User> users = new ArrayList<>(); 
        for (SleeperUserResponse user : sleeperUsers) {
            users.add(new User(user.getUserId(), user.getDisplayName()));
        }
        return users;
    }

    /**
     * Converts sleeper user responses to database user models
     * @param sleeperRosterUsers the sleeper user responses
     * @return the list of users to be inserted to the database
     */
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

    /**
     * Converts sleeper matchup responses to database matchup models
     * @param sleeperMatchups the sleeper matchup responses
     * @return the list of matchups to be inserted to the database
     */
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
