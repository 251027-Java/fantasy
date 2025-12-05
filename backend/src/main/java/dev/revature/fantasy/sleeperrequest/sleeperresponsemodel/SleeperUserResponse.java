package dev.revature.fantasy.sleeperrequest.sleeperresponsemodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SleeperUserResponse {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("display_name")
    private String displayName;
    // TODO: update this to have team name

}
