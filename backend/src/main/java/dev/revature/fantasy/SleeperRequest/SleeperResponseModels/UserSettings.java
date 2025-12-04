package dev.revature.fantasy.sleeperRequest.sleeperResponseModels;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class UserSettings {

    @JsonProperty("wins")
    private int wins;

    @JsonProperty("waiver_position")
    private int waiverPosition;

    @JsonProperty("waiver_budget_used")
    private int waiverBudgetUsed;

    @JsonProperty("total_moves")
    private int totalMoves;

    @JsonProperty("ties")
    private int ties;

    @JsonProperty("losses")
    private int losses;

    @JsonProperty("fpts_decimal")
    private int fptsDecimal;

    @JsonProperty("fpts_against_decimal")
    private int fptsAgainstDecimal;

    @JsonProperty("fpts_against")
    private int fptsAgainst;

    @JsonProperty("fpts")
    private int fpts;

}