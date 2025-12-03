package dev.revature.fantasy.service;

import java.util.List;
import java.util.ArrayList;

import dev.revature.fantasy.dto.*;
import dev.revature.fantasy.model.*;
import dev.revature.fantasy.sleeperRequest.sleeperResponseModels.*;

public class DatabaseFormatter {
    public static List<League> formatLeagueInfo(List<SleeperLeagueResponse> leagues) { 
        List<League> response = new ArrayList<>();

        for (SleeperLeagueResponse league : leagues) {
            response.add(new League(league.getLeagueId(), league.getNumTeams(), league.getName(), league.getSeasonYear()));
        }
        return response;
    }
}
