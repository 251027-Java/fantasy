package dev.revature.fantasy.sleeperrequest;

import dev.revature.fantasy.exception.SleeperException;
import dev.revature.fantasy.logger.GlobalLogger;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperLeagueResponse;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperMatchupResponse;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperNFLStateResponse;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperPlayerResponse;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperRosterUserResponse;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperUserResponse;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.SleeperUsernameResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RequestFormatter is a class used to format the responses from sleeper into
 * POJO
 */
@Component
public class ResponseFormatter {

    private static final ObjectMapper om = new ObjectMapper();

    private static final ParameterizedTypeReference<List<SleeperMatchupResponse>> MATCHUP_LIST_TYPE =
            new ParameterizedTypeReference<List<SleeperMatchupResponse>>() {};

    private final WebClient webClient;

    public ResponseFormatter(WebClient webClient) {
        this.webClient = webClient;
    }

    public static List<SleeperPlayerResponse> getPlayers() {
        try {
            HttpResponse<String> response = SleeperRequestHandler.getPlayers();
            GlobalLogger.debug("Players retrieved from sleeper");
            if (response.statusCode() == 200) {

                Map<String, SleeperPlayerResponse> map =
                        om.readValue(response.body(), new TypeReference<Map<String, SleeperPlayerResponse>>() {});
                List<SleeperPlayerResponse> resp = new ArrayList<>(map.values());

                return resp;
            }
        } catch (Exception e) {
            GlobalLogger.error("Could not get players", e);
        }
        System.out.println("No users found");
        return List.of();
    }

    /**
     * Get leagues for the current year from sleeper based on userId
     *
     * @param userId the user_id of leagues to look for
     * @return list of leagues
     */
    public static List<SleeperLeagueResponse> getLeaguesFromUserId(String userId) {
        // get the current year from time clock
        int year = LocalDate.now().getYear();

        try {
            HttpResponse<String> response = SleeperRequestHandler.getLeaguesFromUserIDAndSeason(userId, year);
            if (response.statusCode() == 200) {
                List<SleeperLeagueResponse> resp =
                        om.readValue(response.body(), new TypeReference<List<SleeperLeagueResponse>>() {});
                return resp;
            }
        } catch (Exception e) {
            GlobalLogger.error(String.format("Could not get leagues from user_id '%s'", userId), e);
        }
        System.out.println("No leagues found");
        return List.of();
    }

    // Returns a JSON object for the userId from a given username. Object model
    // within SleeperUsernameResponse.java
    public static SleeperUsernameResponse getUserIdFromUsername(String username) {
        try {
            HttpResponse<String> response = SleeperRequestHandler.getUserFromUsername(username);
            if (response.statusCode() == 200) {
                SleeperUsernameResponse resp = om.readValue(response.body(), SleeperUsernameResponse.class);
                return resp;
            }
        } catch (Exception e) {
            GlobalLogger.error(String.format("Could not get user from username '%s'", username), e);
        }
        System.out.println("No user found");
        return null;
    }

    /**
     * Get users from a sleeper league based on leagueId
     *
     * @param leagueId the id of the league
     * @return a list of users
     */
    public static List<SleeperUserResponse> getUsersFromLeague(String leagueId) {
        try {
            HttpResponse<String> response = SleeperRequestHandler.getUsersFromLeague(leagueId);
            if (response.statusCode() == 200) {
                List<SleeperUserResponse> resp =
                        om.readValue(response.body(), new TypeReference<List<SleeperUserResponse>>() {});
                return resp;
            }
        } catch (Exception e) {
            GlobalLogger.error(String.format("Could not get users from league_id '%s'", leagueId), e);
        }
        System.out.println("No users found");
        return List.of();
    }

    public static List<SleeperRosterUserResponse> getRostersFromLeagueId(String leagueId) {
        try {
            HttpResponse<String> response = SleeperRequestHandler.getRostersFromLeague(leagueId);
            if (response.statusCode() == 200) {
                List<SleeperRosterUserResponse> resp =
                        om.readValue(response.body(), new TypeReference<List<SleeperRosterUserResponse>>() {});
                return resp;
            }
        } catch (Exception e) {
            GlobalLogger.error(String.format("Could not get rosters from league_id '%s'", leagueId), e);
        }
        System.out.println("No rosters found");
        return List.of();
    }

    /**
     * Retrieves matchups for a specific league and week, returning a Mono
     * that will emit the list of matchups.
     * * @param leagueId The league ID.
     * @param weekNum The week number.
     * @return Mono<List<SleeperMatchupResponse>>
     */
    public Mono<List<SleeperMatchupResponse>> nonBlockGetMatchupsFromLeagueIdAndWeek(String leagueId, int weekNum) {

        // Define the API endpoint URL
        String uri = String.format("/league/%s/matchups/%d", leagueId, weekNum);

        return webClient
                .get()
                .uri(uri)
                .retrieve()
                // handle HTTP errors
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                    GlobalLogger.error(String.format(
                            "API returned error for league_id '%s' week '%d', status: %d",
                            leagueId, weekNum, clientResponse.statusCode().value()));
                    return Mono.error(new SleeperException("Error fetching matchups: " + clientResponse.statusCode()));
                })
                .bodyToMono(MATCHUP_LIST_TYPE)
                // handle any exceptions (HTTP errors, deserialization errors, etc.)
                .onErrorResume(Exception.class, e -> {
                    System.out.println("No matchups found (or error occurred)");
                    return Mono.just(List.of());
                });
    }

    public static List<SleeperMatchupResponse> getMatchupsFromLeagueIdAndWeek(String leagueId, int weekNum) {
        try {
            HttpResponse<String> response = SleeperRequestHandler.getMatchupsFromLeagueIdAndWeek(leagueId, weekNum);
            if (response.statusCode() == 200) {
                List<SleeperMatchupResponse> resp =
                        om.readValue(response.body(), new TypeReference<List<SleeperMatchupResponse>>() {});
                return resp;
            }
        } catch (Exception e) {
            GlobalLogger.error(
                    String.format("Could not get matchups from league_id '%s' for week '%d'", leagueId, weekNum), e);
        }
        System.out.println("No matchups found");
        return List.of();
    }

    public static SleeperNFLStateResponse getNFLState() {
        try {
            HttpResponse<String> response = SleeperRequestHandler.getNFLState();
            if (response.statusCode() == 200) {
                SleeperNFLStateResponse resp = om.readValue(response.body(), SleeperNFLStateResponse.class);
                return resp;
            }
        } catch (Exception e) {
            GlobalLogger.error("Could not get nfl state", e);
        }
        System.out.println("No nfl state found");
        return null;
    }
}
