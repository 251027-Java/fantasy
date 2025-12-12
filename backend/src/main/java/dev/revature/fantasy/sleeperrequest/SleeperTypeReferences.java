package dev.revature.fantasy.sleeperrequest; // Adjust package as necessary

import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.*; // Import all necessary POJOs

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import tools.jackson.core.type.TypeReference;

public class SleeperTypeReferences {
    private static final TypeReference<List<SleeperMatchupResponse>> MATCHUP_LIST_TYPE = new TypeReference<List<SleeperMatchupResponse>>() {};
    private static final TypeReference<Map<String, SleeperPlayerResponse>> PLAYER_MAP_TYPE = new TypeReference<Map<String, SleeperPlayerResponse>>() {};
    private static final TypeReference<List<SleeperUserResponse>> USER_LIST_TYPE = new TypeReference<List<SleeperUserResponse>>() {};
    private static final TypeReference<List<SleeperRosterUserResponse>> ROSTER_USER_LIST_TYPE = new TypeReference<List<SleeperRosterUserResponse>>() {};
    private static final TypeReference<List<SleeperLeagueResponse>> LEAGUE_LIST_TYPE = new TypeReference<List<SleeperLeagueResponse>>() {};
    private static final TypeReference<SleeperUsernameResponse> USERNAME_TYPE = new TypeReference<SleeperUsernameResponse>() {};
    private static final TypeReference<SleeperNFLStateResponse> NFL_STATE_TYPE = new TypeReference<SleeperNFLStateResponse>() {};

    public static TypeReference<List<SleeperMatchupResponse>> getMatchupListType() {
        return MATCHUP_LIST_TYPE;
    }
    public static TypeReference<Map<String, SleeperPlayerResponse>> getPlayerMapType() {
        return PLAYER_MAP_TYPE;
    }
    public static TypeReference<List<SleeperUserResponse>> getUserListType() {
        return USER_LIST_TYPE;
    }
    public static TypeReference<List<SleeperRosterUserResponse>> getRosterUserListType() {
        return ROSTER_USER_LIST_TYPE;
    }
    public static TypeReference<List<SleeperLeagueResponse>> getLeagueListType() {
        return LEAGUE_LIST_TYPE;
    }
    public static TypeReference<SleeperUsernameResponse> getUsernameType() {
        return USERNAME_TYPE;
    }
    public static TypeReference<SleeperNFLStateResponse> getNFLStateType() {
        return NFL_STATE_TYPE;
    }
}