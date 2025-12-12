package dev.revature.fantasy.sleeperrequest;

import dev.revature.fantasy.exception.HttpConnectionException;

import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.*;
import org.apache.http.HttpStatus;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.json.JsonParseException;
import org.springframework.web.reactive.function.client.WebClient;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class ResponseFormatterTest {

    @Mock
    private SleeperRequestHandler sleeperRequestHandler;

    @Mock
    private WebClient webClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;


    @Mock
    private ObjectMapper mockObjectMapper;

    @InjectMocks
    private ResponseFormatter responseFormatter;



    private final String TEST_LEAGUE_ID = "123";
    private final String TEST_USER_ID = "456";
    private final String TEST_USERNAME = "tester";
    
    
    @Test
    void getPlayers_Success_ReturnsListOfPlayers() throws Exception {
        // arrange
        final String mockResponseBody = 
                "{\"1\": {\"player_id\": \"1\"}, " +
                "\"2\": {\"player_id\": \"2\"} }";
        final var player1 = new SleeperPlayerResponse("1", List.of("QB"), "team", "first_name", "last_name", 1, "1", 1);
        final var player2 = new SleeperPlayerResponse("2", List.of("WR"), "team", "first_name1", "last_name2", 2, "2", 2);
        
        final Map<String, SleeperPlayerResponse> expectedDeserializeResponse = Map.of(
                "1", player1,
                "2", player2
        );

        final List<SleeperPlayerResponse> expectedResponse = List.of(player1, player2);

        when(sleeperRequestHandler.getPlayers()).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);
       

        doReturn(expectedDeserializeResponse)
            .when(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getPlayerMapType()));
        // act
        List<SleeperPlayerResponse> result = responseFormatter.getPlayers();

        // assert
        // check that they have the same players, not necessarily same order
        assertEquals(new HashSet<>(expectedResponse), new HashSet<>(result));
        verify(mockHttpResponse).body();
        verify(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getPlayerMapType()));
        verify(sleeperRequestHandler).getPlayers();
    }

    @Test
    void getPlayers_HttpFailure_ReturnsEmptyList() throws Exception {
        // arrange
        when(sleeperRequestHandler.getPlayers()).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_NOT_FOUND); // 404

        // act
        List<SleeperPlayerResponse> result = responseFormatter.getPlayers();

        // assert
        assertTrue(result.isEmpty());
        verify(mockHttpResponse).statusCode();
        verify(mockObjectMapper, never()).readValue(anyString(), eq(SleeperTypeReferences.getPlayerMapType()));
        verify(sleeperRequestHandler).getPlayers();
    }

    @Test
    void getPlayers_HandlerThrowsException_ReturnsEmptyList() throws Exception {
        // arrange
        when(sleeperRequestHandler.getPlayers()).thenThrow(new HttpConnectionException("Network error"));

        // act & assert
        var res = responseFormatter.getPlayers();
        assertTrue(res.isEmpty());
        verify(mockHttpResponse, never()).statusCode();
        verify(mockObjectMapper, never()).readValue(anyString(), eq(SleeperTypeReferences.getPlayerMapType()));
        verify(sleeperRequestHandler).getPlayers();
    }

    @Test
    void getLeaguesFromUserId_Success_ReturnsListOfLeagues() throws Exception {
        // arrange
        int currentYear = LocalDate.now().getYear();
        final String mockResponseBody = "[{\"league_id\": \"1\"}]";
        SleeperLeagueResponse mockLeague = new SleeperLeagueResponse(10, currentYear, "1", "1", "leagueName");
        
        when(sleeperRequestHandler.getLeaguesFromUserIDAndSeason(TEST_USER_ID, currentYear)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(List.of(mockLeague))
            .when(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getLeagueListType()));

        // act
        List<SleeperLeagueResponse> result = responseFormatter.getLeaguesFromUserId(TEST_USER_ID);

        // assert
        assertEquals(1, result.size());
        assertEquals(mockLeague, result.get(0));
        verify(mockHttpResponse).statusCode();
        verify(mockHttpResponse).body();
        verify(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getLeagueListType()));
        verify(sleeperRequestHandler).getLeaguesFromUserIDAndSeason(TEST_USER_ID, currentYear);
    }

    @Test
    void getLeaguesFromUserId_DeserializationError_ReturnsEmptyList() throws Exception {
        // arrange
        int currentYear = LocalDate.now().getYear();
        final String badResponseBody = "Invalid JSON";
        
        when(sleeperRequestHandler.getLeaguesFromUserIDAndSeason(TEST_USER_ID, currentYear)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(badResponseBody);
        
        // (simulate jackson error)
        doThrow(new JsonParseException())
            .when(mockObjectMapper).readValue(eq(badResponseBody), eq(SleeperTypeReferences.getLeagueListType()));

        // act
        List<SleeperLeagueResponse> result = responseFormatter.getLeaguesFromUserId(TEST_USER_ID);

        // assert
        assertTrue(result.isEmpty());
        verify(mockHttpResponse).statusCode();
        verify(mockHttpResponse).body();
        verify(mockObjectMapper).readValue(anyString(), eq(SleeperTypeReferences.getLeagueListType()));
        verify(sleeperRequestHandler).getLeaguesFromUserIDAndSeason(TEST_USER_ID, currentYear);
    }
    
    // --- Test Cases for getUserIdFromUsername() ---

    @Test
    void getUserIdFromUsername_Success_ReturnsUserId() throws Exception {
        // arrange
        final String mockResponseBody = "{\"user_id\": \"456\"}";
        SleeperUsernameResponse mockUserResponse = new SleeperUsernameResponse();
        mockUserResponse.setUserId(TEST_USER_ID);
        
        when(sleeperRequestHandler.getUserFromUsername(TEST_USERNAME)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(mockUserResponse)
            .when(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getUsernameType()));

        // ACT
        SleeperUsernameResponse result = responseFormatter.getUserIdFromUsername(TEST_USERNAME);

        // ASSERT
        assertNotNull(result);
        verify(sleeperRequestHandler).getUserFromUsername(TEST_USERNAME);
        verify(mockHttpResponse).statusCode();
        verify(mockHttpResponse).body();
        verify(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getUsernameType()));
        assertEquals(TEST_USER_ID, result.getUserId());
    }
    
    @Test
    void getUserIdFromUsername_NotFound_ReturnsNull() throws Exception {
        // arrange
        when(sleeperRequestHandler.getUserFromUsername(TEST_USERNAME)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_NOT_FOUND); // 404

        // act
        SleeperUsernameResponse result = responseFormatter.getUserIdFromUsername(TEST_USERNAME);

        // assert
        verify(mockHttpResponse).statusCode();
        verify(sleeperRequestHandler).getUserFromUsername(TEST_USERNAME);
        verify(mockObjectMapper, never()).readValue(anyString(), eq(SleeperTypeReferences.getUsernameType()));
        verify(mockHttpResponse, never()).body();
        assertNull(result);
    }
    
    

    @Test
    void getUsersFromLeague_Success_ReturnsListOfUsers() throws Exception {
        // arrange
        final String mockResponseBody = "[{\"user_id\": \"1\"}]";
        SleeperUserResponse mockUser = new SleeperUserResponse();
        
        when(sleeperRequestHandler.getUsersFromLeague(TEST_LEAGUE_ID)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(List.of(mockUser))
            .when(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getUserListType()));

        // act
        List<SleeperUserResponse> result = responseFormatter.getUsersFromLeague(TEST_LEAGUE_ID);

        // assert
        verify(mockHttpResponse).statusCode();
        verify(mockHttpResponse).body();
        verify(sleeperRequestHandler).getUsersFromLeague(TEST_LEAGUE_ID);
        verify(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getUserListType()));
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    // --- Test Cases for getRostersFromLeagueId() ---

    @Test
    void getRostersFromLeagueId_Success_ReturnsListOfRosters() throws Exception {
        // arrange
        final String mockResponseBody = "[{\"roster_id\": 1}]";
        SleeperRosterUserResponse mockRoster = new SleeperRosterUserResponse();
        
        when(sleeperRequestHandler.getRostersFromLeague(TEST_LEAGUE_ID)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(List.of(mockRoster))
            .when(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getRosterUserListType()));

        // act
        List<SleeperRosterUserResponse> result = responseFormatter.getRostersFromLeagueId(TEST_LEAGUE_ID);

        // assert
        verify(mockHttpResponse).statusCode();
        verify(mockHttpResponse).body();
        verify(sleeperRequestHandler).getRostersFromLeague(TEST_LEAGUE_ID);
        verify(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getRosterUserListType()));
        assertEquals(1, result.size());
        assertEquals(result.get(0), mockRoster);
    }
    
    // --- Test Cases for getMatchupsFromLeagueIdAndWeek (Blocking) ---

    @Test
    void getMatchupsFromLeagueIdAndWeek_Success_ReturnsListOfMatchups() throws Exception {
        // arrange
        final int testWeek = 5;
        final String mockResponseBody = "[{\"matchup_id\": 1}]";
        SleeperMatchupResponse mockMatchup = new SleeperMatchupResponse();
        
        when(sleeperRequestHandler.getMatchupsFromLeagueIdAndWeek(TEST_LEAGUE_ID, testWeek)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(List.of(mockMatchup))
            .when(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getMatchupListType()));

        // ACT
        List<SleeperMatchupResponse> result = responseFormatter.getMatchupsFromLeagueIdAndWeek(TEST_LEAGUE_ID, testWeek);

        // ASSERT
        verify(mockHttpResponse).statusCode();
        verify(mockHttpResponse).body();
        verify(sleeperRequestHandler).getMatchupsFromLeagueIdAndWeek(TEST_LEAGUE_ID, testWeek);
        verify(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getMatchupListType()));
        assertEquals(1, result.size());
        assertEquals(result.get(0), mockMatchup);
    }
    
    // --- Test Cases for getNFLState() ---

    @Test
    void getNFLState_Success_ReturnsNFLStateObject() throws Exception {
        // arrange
        final String mockResponseBody = "{\"week\": 10, \"season\": \"2024\"}";
        SleeperNFLStateResponse mockState = new SleeperNFLStateResponse();
        mockState.setWeek("10");
        
        when(sleeperRequestHandler.getNFLState()).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(mockState)
            .when(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getNFLStateType()));

        // act
        SleeperNFLStateResponse result = responseFormatter.getNFLState();

        // assert
        assertEquals(mockState, result);
        verify(mockHttpResponse).statusCode();
        verify(mockHttpResponse).body();
        verify(mockObjectMapper).readValue(eq(mockResponseBody), eq(SleeperTypeReferences.getNFLStateType()));
    }

    // // --- Test Cases for nonBlockGetMatchupsFromLeagueIdAndWeek (Reactive) ---

    // // This requires mocking the WebClient's fluent API chain (get().uri().retrieve()...)
    // @Test
    // void nonBlockGetMatchupsFromLeagueIdAndWeek_Success_ReturnsMonoOfMatchups() {
    //     // ARRANGE
    //     final int testWeek = 5;
    //     final String uri = String.format("/league/%s/matchups/%d", TEST_LEAGUE_ID, testWeek);
    //     SleeperMatchupResponse mockMatchup = new SleeperMatchupResponse();
    //     List<SleeperMatchupResponse> expectedList = List.of(mockMatchup);

    //     // Mock the WebClient chain
    //     WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    //     WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    //     WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    //     when(webClient.get()).thenReturn(requestHeadersUriSpec);
    //     when(requestHeadersUriSpec.uri(uri)).thenReturn(requestHeadersSpec);
    //     when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
    //     // Mocking the successful bodyToMono call
    //     // The cast to ParameterizedTypeReference is necessary due to generics/type erasure
    //     when(responseSpec.bodyToMono(any()))
    //         .thenReturn(Mono.just(expectedList));

    //     // ACT
    //     Mono<List<SleeperMatchupResponse>> resultMono = 
    //         responseFormatter.nonBlockGetMatchupsFromLeagueIdAndWeek(TEST_LEAGUE_ID, testWeek);

    //     // ASSERT
    //     // Block for testing, but the method returns a Mono
    //     List<SleeperMatchupResponse> result = resultMono.block(); 
    //     assertNotNull(result);
    //     assertFalse(result.isEmpty());
    //     assertEquals(1, result.size());
    // }

    // @Test
    // void nonBlockGetMatchupsFromLeagueIdAndWeek_Http404_ReturnsEmptyMono() {
    //     // ARRANGE
    //     final int testWeek = 5;
    //     final String uri = String.format("/league/%s/matchups/%d", TEST_LEAGUE_ID, testWeek);

    //     WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    //     WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
    //     WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
    //     ClientResponse mockClientResponse = mock(ClientResponse.class);

    //     when(webClient.get()).thenReturn(requestHeadersUriSpec);
    //     when(requestHeadersUriSpec.uri(uri)).thenReturn(requestHeadersSpec);
    //     when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    //     // Mock the onStatus part for 4xx/5xx error handling
    //     when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
    //         // Get the status predicate and handler function from the invocation arguments
    //         java.util.function.Predicate<org.springframework.http.HttpStatus> predicate = invocation.getArgument(0);
    //         java.util.function.Function<ClientResponse, Mono<? extends Throwable>> handlerFunction = invocation.getArgument(1);

    //         // If the status is 4xx or 5xx (predicate returns true), call the handler
    //         if (predicate.test(org.springframework.http.HttpStatus.NOT_FOUND)) { 
    //             when(mockClientResponse.statusCode()).thenReturn(org.springframework.http.HttpStatus.NOT_FOUND);
    //             return handlerFunction.apply(mockClientResponse); // Returns Mono<SleeperException>
    //         }
    //         return responseSpec;
    //     });

    //     // Mock the remaining bodyToMono part to return the error
    //     when(responseSpec.bodyToMono(any(ResponseFormatter.MATCHUP_LIST_TYPE.getType())))
    //         .thenReturn(Mono.error(new SleeperException("Simulated error")));

    //     // ACT
    //     Mono<List<SleeperMatchupResponse>> resultMono = 
    //         responseFormatter.nonBlockGetMatchupsFromLeagueIdAndWeek(TEST_LEAGUE_ID, testWeek);

    //     // ASSERT
    //     // Verify that the onErrorResume caught the SleeperException and returned an empty list
    //     List<SleeperMatchupResponse> result = resultMono.block(); 
    //     assertNotNull(result);
    //     assertTrue(result.isEmpty());
    // }


}