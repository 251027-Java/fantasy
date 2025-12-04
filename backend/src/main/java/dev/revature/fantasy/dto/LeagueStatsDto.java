package dev.revature.fantasy.dto;

import lombok.Data;
import lombok.NonNull;


@Data
public class LeagueStatsDto {
    @NonNull
    private StatDto[] stats;
}
