package dev.revature.fantasy.sleeperrequest;

import dev.revature.fantasy.exception.HttpConnectionException;
import dev.revature.fantasy.logger.GlobalLogger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.springframework.stereotype.Component;

@Component
public class SleeperRequestHandler {

    private final HttpClient client;
    private static String baseUrl = "https://api.sleeper.app/v1";

    public SleeperRequestHandler() {
        // Default constructor for production use, creates a single reusable client instance
        this.client = HttpClient.newHttpClient();
    }

    // Constructor for dependency injection and testing
    public SleeperRequestHandler(HttpClient client) {
        this.client = client;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Get all the players from api. Returns a large response (multiple megabytes).
     *
     * @return The response from sleeper api
     * @throws HttpConnectionException if an I/O error occurs when
     * sending or receiving or if the request is
     * interrupted
     */
    public HttpResponse<String> getPlayers() throws HttpConnectionException {
        // build request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/players/nfl"))
                .GET()
                .build();

        // send the request and get the response
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            GlobalLogger.error("Could not get players", e);
            throw new HttpConnectionException("Error requesting players from sleeper api");
        }
    }

    /**
     * Get sleeper user from username
     *
     * @param username
     * @return The response from sleeper api
     * @throws HttpConnectionException if an I/O error occurs when
     * sending or receiving or if the request is
     * interrupted
     */
    public HttpResponse<String> getUserFromUsername(String username) throws HttpConnectionException {
        // build an HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/user/" + username + "/"))
                .GET()
                .build();

        // send the request and get the response
        try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            GlobalLogger.error(String.format("Could not get user_id from username '%s'", username), e);
            throw new HttpConnectionException("Error getting user from sleeper api");
        }
    }

    /**
     * Get all the leagues from a sleeper user in a given year from api
     *
     * @param user_id the user_id from sleeper
     * @param season  the year to get the leagues
     * @return The response from sleeper api
     * @throws HttpConnectionException if an I/O error occurs when
     * sending or receiving or if the request is
     * interrupted
     */
    public HttpResponse<String> getLeaguesFromUserIDAndSeason(String userId, int seasonYear)
            throws HttpConnectionException {

        // build an HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/user/" + userId + "/leagues/nfl/" + seasonYear))
                .GET()
                .build();

        // send the request and get the response
        try {
            GlobalLogger.debug("Making " + request.method() + " request to "
                    + request.uri().toString());
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            GlobalLogger.error(
                    String.format("Could not get user '%s' leagues from season '%s'", userId, seasonYear), e);
            throw new HttpConnectionException("Error getting leagues from sleeper api");
        }
    }

    /**
     * Get all the users from a sleeper league
     * @param leagueId the Id of the league to get users from
     * @return The response from sleeper api
     * @throws HttpConnectionException if an I/O error occurs when
     * sending or receiving or if the request is
     * interrupted
     */
    public HttpResponse<String> getUsersFromLeague(String leagueId) throws HttpConnectionException {
        // build an HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/league/" + leagueId + "/users"))
                .GET()
                .build();

        // send the request and get the response
        try {
            GlobalLogger.debug("Making " + request.method() + " request to "
                    + request.uri().toString());
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            GlobalLogger.error(String.format("Could not get users for league with ID '%s'", leagueId), e);
            throw new HttpConnectionException("Error getting leagues from sleeper api");
        }
    }

    /**
     * Get all the current rosters from a sleeper league
     * @param leagueId the Id of the league to get rosters from
     * @return The response from sleeper api
     * @throws HttpConnectionException
     */
    public HttpResponse<String> getRostersFromLeague(String leagueId) throws HttpConnectionException {
        // build an HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/league/" + leagueId + "/rosters"))
                .GET()
                .build();

        // send the request and get the response
        try {
            GlobalLogger.debug("Making " + request.method() + " request to "
                    + request.uri().toString());
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            GlobalLogger.error(String.format("Could not get rosters for league with ID '%s'", leagueId), e);
            throw new HttpConnectionException("Error getting rosters from sleeper api");
        }
    }

    public HttpResponse<String> getMatchupsFromLeagueIdAndWeek(String leagueId, int weekNum)
            throws HttpConnectionException {
        // build an HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/league/" + leagueId + "/matchups/" + weekNum))
                .GET()
                .build();

        // send the request and get the response
        try {
            GlobalLogger.debug("Making " + request.method() + " request to "
                    + request.uri().toString());
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            GlobalLogger.error(String.format("Could not get rosters for league with ID '%s'", leagueId), e);
            throw new HttpConnectionException("Error getting rosters from sleeper api");
        }
    }

    public HttpResponse<String> getNFLState() throws HttpConnectionException {
        // build an HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/state/nfl"))
                .GET()
                .build();

        // send the request and get the response
        try {
            GlobalLogger.debug("Making " + request.method() + " request to "
                    + request.uri().toString());
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            GlobalLogger.error(String.format("Could not get rosters for league with ID '%s'", baseUrl), e);
            throw new HttpConnectionException("Error getting rosters from sleeper api");
        }
    }
}