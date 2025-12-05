package dev.revature.fantasy.sleeperrequest.sleeperresponsemodel;

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
public class SleeperLeagueResponse {
    @JsonProperty("total_rosters")
    private int numTeams;

    @JsonProperty("season")
    private int seasonYear;

    @JsonProperty("league_id")
    private String leagueId;

    @JsonProperty("draft_id")
    private String draftId;

    @JsonProperty("name")
    private String name;
}
