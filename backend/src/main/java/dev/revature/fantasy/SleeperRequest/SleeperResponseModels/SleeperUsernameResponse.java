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
public class SleeperUsernameResponse {

    @JsonProperty("user_id")
    private String userId;


}