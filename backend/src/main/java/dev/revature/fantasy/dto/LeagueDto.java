package dev.revature.fantasy.dto;

import lombok.Data;
import lombok.NonNull;

//
@Data
public class LeagueDto {
    @NonNull
    private String leagueId;

    @NonNull
    private String leagueName;
}
