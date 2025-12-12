package dev.revature.fantasy.sleeperrequest;

import dev.revature.fantasy.exception.HttpConnectionException;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SleeperRequestHandlerTest {

    @Mock
    private HttpClient mockHttpClient;


    @Mock
    private HttpResponse<String> mockHttpResponse;
    
    @InjectMocks
    private SleeperRequestHandler handler;
    private static final String BASE_URL = "https://api.sleeper.app/v1";

    @Test
    void getPlayers_Success_ReturnsResponse() throws Exception {
        // arrange
        final String expectedBody = "{\"players\": [{\"player_id\": \"123\"}] }";
        final String expectedUri = BASE_URL + "/players/nfl";
        when(mockHttpResponse.body()).thenReturn(expectedBody);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockHttpResponse);

        // act
        HttpResponse<String> actualResponse = handler.getPlayers();

        // assert
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient, times(1)).send(requestCaptor.capture(), any());
        assertEquals(expectedUri, requestCaptor.getValue().uri().toString(), "The request URI should be correct.");
        assertEquals(HttpStatus.SC_OK, actualResponse.statusCode(), "The status code should match the mock.");
        assertEquals(expectedBody, actualResponse.body(), "The body should match the mock.");
    }

    @Test
    void getPlayers_NetworkError_ThrowsHttpConnectionException() throws Exception {
        // arrange
        when(mockHttpClient.send(any(HttpRequest.class), any()))
            .thenThrow(new IOException("Simulated network failure"));

        // act & assert
        assertThrows(HttpConnectionException.class, () -> {
            handler.getPlayers();
        }, "Should throw HttpConnectionException on network error.");
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void getUserFromUsername_Success_VerifiesCorrectUri() throws Exception {
        // arrange
        final String testUsername = "testuser";
        final String expectedUri = BASE_URL + "/user/testuser/";
        final String expectedBody = "{ \"user_id\": \"123\" }";
        when(mockHttpResponse.body()).thenReturn(expectedBody);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);

        // act
        var actualResponse = handler.getUserFromUsername(testUsername);

        // assert
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        assertEquals(expectedBody, actualResponse.body(), "The body should match the mock.");
        assertEquals(HttpStatus.SC_OK, actualResponse.statusCode(), "The status code should match the mock.");
        verify(mockHttpClient).send(requestCaptor.capture(), any());
        verify(mockHttpResponse).statusCode();
        assertEquals(expectedUri, requestCaptor.getValue().uri().toString(), "The request URI should include the username.");
    }

    @Test
    void getUserFromUsername_InterruptedError_ThrowsHttpConnectionException() throws Exception {
        // arrange
        // Simulate an interruption during the send process
        when(mockHttpClient.send(any(HttpRequest.class), any()))
            .thenThrow(new InterruptedException("Simulated thread interruption"));

        // act & assert
        assertThrows(HttpConnectionException.class, () -> {
            handler.getUserFromUsername("anyUser");
        }, "Should throw HttpConnectionException on thread interruption.");
    }

    @Test
    void getLeaguesFromUserIDAndSeason_Success_VerifiesParameters() throws Exception {
        // arrange
        final String testUserId = "987";
        final int testSeason = 2024;
        final String expectedUri = BASE_URL + "/user/987/leagues/nfl/2024";
        final String expectedBodyString = "{ \"leagues\": [{\"league_id\": 123}] }";
        when(mockHttpResponse.body()).thenReturn(expectedBodyString);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);

        // act
        HttpResponse<String> actualResponse = handler.getLeaguesFromUserIDAndSeason(testUserId, testSeason);

        // assert
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        assertEquals(expectedBodyString, actualResponse.body(), "The body should match the mock.");
        assertEquals(HttpStatus.SC_OK, actualResponse.statusCode(), "The status code should match the mock.");
        verify(mockHttpClient).send(requestCaptor.capture(), any());
        assertEquals(expectedUri, requestCaptor.getValue().uri().toString(), "The URI must combine user ID and season year.");
    }

    @Test
    void getLeaguesFromUserIdAndSeason_InterruptedError_ThrowsHttpConnectionException() throws Exception {
        // arrange
        // Simulate an interruption during the send process
        when(mockHttpClient.send(any(HttpRequest.class), any()))
            .thenThrow(new InterruptedException("Simulated thread interruption"));

        // act & assert
        assertThrows(HttpConnectionException.class, () -> {
            handler.getLeaguesFromUserIDAndSeason("anyUserId", 2024);
        }, "Should throw HttpConnectionException on thread interruption.");
    }

    @Test
    void getUsersFromLeague_Success_VerifiesCorrectUri() throws Exception {
        // arrange
        final String testLeagueId = "L456";
        final String expectedUri = BASE_URL + "/league/L456/users";
        final String expectedBody = "[{\"display_name\": \"UserA\"}]";
        when(mockHttpResponse.body()).thenReturn(expectedBody);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);

        // act
        HttpResponse<String> actualResponse = handler.getUsersFromLeague(testLeagueId);

        // assert
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());
        assertEquals(expectedUri, requestCaptor.getValue().uri().toString(), "The URI should include the league ID.");
        assertEquals(HttpStatus.SC_OK, actualResponse.statusCode(), "The status code should be 200.");
        assertEquals(expectedBody, actualResponse.body(), "The body should match the mock.");
    }

    @Test
    void getUsersFromLeague_Error_ThrowsHttpConnectionException() throws Exception {
        // arrange
        when(mockHttpClient.send(any(HttpRequest.class), any()))
            .thenThrow(new IOException("Timeout"));

        // act & assert
        assertThrows(HttpConnectionException.class, () -> {
            handler.getUsersFromLeague("L456");
        }, "Should throw HttpConnectionException on I/O error.");
    }

    @Test
    void getRostersFromLeague_Success_VerifiesCorrectUri() throws Exception {
        // arrange
        final String testLeagueId = "L789";
        final String expectedUri = BASE_URL + "/league/L789/rosters";
        final String expectedBody = "[{\"roster_id\": 1, \"players\": [...]}]";
        when(mockHttpResponse.body()).thenReturn(expectedBody);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);

        // act
        HttpResponse<String> actualResponse = handler.getRostersFromLeague(testLeagueId);

        // assert
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());
        assertEquals(expectedBody, actualResponse.body());
        assertEquals(expectedUri, requestCaptor.getValue().uri().toString(), "The URI should include the league ID for rosters.");
        assertEquals(HttpStatus.SC_OK, actualResponse.statusCode(), "The status code should be 200.");
    }
    
    @Test
    void getRostersFromLeague_Error_ThrowsHttpConnectionException() throws Exception {
        // arrange
        when(mockHttpClient.send(any(HttpRequest.class), any()))
            .thenThrow(new InterruptedException("Connection reset"));

        // act & assert
        assertThrows(HttpConnectionException.class, () -> {
            handler.getRostersFromLeague("L789");
        }, "Should throw HttpConnectionException on thread interruption.");
    }

    @Test
    void getMatchupsFromLeagueIdAndWeek_Success_VerifiesCorrectUri() throws Exception {
        // arrange
        final String testLeagueId = "L001";
        final int testWeek = 5;
        final String expectedUri = BASE_URL + "/league/L001/matchups/5";
        final String expectedBody = "[{\"matchup_id\": 1, \"starters\": [...]}]";
        when(mockHttpResponse.body()).thenReturn(expectedBody);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);

        // act
        HttpResponse<String> actualResponse = handler.getMatchupsFromLeagueIdAndWeek(testLeagueId, testWeek);

        // assert
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());
        assertEquals(expectedBody, actualResponse.body());
        assertEquals(expectedUri, requestCaptor.getValue().uri().toString(), "The URI should include both league ID and week number.");
        assertEquals(HttpStatus.SC_OK, actualResponse.statusCode(), "The status code should be 200.");
    }
    
    @Test
    void getMatchupsFromLeagueIdAndWeek_Error_ThrowsHttpConnectionException() throws Exception {
        // arrange
        when(mockHttpClient.send(any(HttpRequest.class), any()))
            .thenThrow(new IOException("Bad gateway"));

        // act & assert
        assertThrows(HttpConnectionException.class, () -> {
            handler.getMatchupsFromLeagueIdAndWeek("L001", 5);
        }, "Should throw HttpConnectionException on I/O error.");
    }

    @Test
    void getNFLState_Success_VerifiesCorrectUri() throws Exception {
        // arrange
        final String expectedUri = BASE_URL + "/state/nfl";
        final String expectedBody = "{\"week\": 12, \"season\": \"2024\"}";
        when(mockHttpResponse.body()).thenReturn(expectedBody);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(mockHttpResponse);

        // act
        HttpResponse<String> actualResponse = handler.getNFLState();

        // assert
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).send(requestCaptor.capture(), any());
        assertEquals(expectedUri, requestCaptor.getValue().uri().toString(), "The URI should target the NFL state endpoint.");
        assertEquals(HttpStatus.SC_OK, actualResponse.statusCode(), "The status code should be 200.");
        assertEquals(expectedBody, actualResponse.body(), "The body should match the mock.");
    }
    
    @Test
    void getNFLState_Error_ThrowsHttpConnectionException() throws Exception {
        // arrange
        when(mockHttpClient.send(any(HttpRequest.class), any()))
            .thenThrow(new InterruptedException("Service unavailable"));

        // act & assert
        assertThrows(HttpConnectionException.class, () -> {
            handler.getNFLState();
        }, "Should throw HttpConnectionException on thread interruption.");
    }
}