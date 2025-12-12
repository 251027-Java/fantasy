package dev.revature.fantasy.sleeperrequest;

import dev.revature.fantasy.exception.HttpConnectionException;
import dev.revature.fantasy.exception.SleeperException;
import dev.revature.fantasy.sleeperrequest.sleeperresponsemodel.*;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;



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



    private final String TEST_LEAGUE_ID = "L123";
    private final String TEST_USER_ID = "U456";
    private final String TEST_USERNAME = "tester";
    
    
    @Test
    void getPlayers_Success_ReturnsListOfPlayers() throws Exception {
        // ARRANGE
        final String mockResponseBody = 
                "{\"1\": {\"player_id\": \"1\"}, \"2\": {\"player_id\": \"2\"}}";
        final List<SleeperPlayerResponse> expectedResponse = List.of(
                new SleeperPlayerResponse("1", List.of("QB"), "team", "first_name", "last_name", 1, "1", 1),
                new SleeperPlayerResponse("2", List.of("WR"), "team", "first_name1", "last_name2", 1, "2", 2)
        );
        TypeReference<List<SleeperPlayerResponse>> typeRef = new TypeReference<List<SleeperPlayerResponse>>() {};
        
    
        // Mock the HTTP Handler response
        when(sleeperRequestHandler.getPlayers()).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);
        var type = convert(typeRef);

        @SuppressWarnings("unchecked")
        when(mockObjectMapper).readValue(
                anyString(), 
                any(type.getClass()) 
            ).thenReturn(expectedResponse);
        // ACT
        List<SleeperPlayerResponse> result = responseFormatter.getPlayers();

        // ASSERT
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        verify(sleeperRequestHandler).getPlayers();
    }

    @Test
    void getPlayers_HttpFailure_ReturnsEmptyList() throws Exception {
        // ARRANGE
        when(sleeperRequestHandler.getPlayers()).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_NOT_FOUND); // 404

        // ACT
        List<SleeperPlayerResponse> result = responseFormatter.getPlayers();

        // ASSERT
        assertTrue(result.isEmpty());
        verify(om, never()).readValue(anyString(), any(tools.jackson.core.type.TypeReference.class));
    }

    @Test
    void getPlayers_HandlerThrowsException_ReturnsEmptyList() throws Exception {
        // ARRANGE
        when(sleeperRequestHandler.getPlayers()).thenThrow(new HttpConnectionException("Network error"));

        // ACT
        List<SleeperPlayerResponse> result = responseFormatter.getPlayers();

        // ASSERT
        assertTrue(result.isEmpty());
    }

    // --- Test Cases for getLeaguesFromUserId() ---

    @Test
    void getLeaguesFromUserId_Success_ReturnsListOfLeagues() throws Exception {
        // ARRANGE
        int currentYear = LocalDate.now().getYear();
        final String mockResponseBody = "[{\"league_id\": \"1\"}]";
        SleeperLeagueResponse mockLeague = new SleeperLeagueResponse();
        
        when(sleeperRequestHandler.getLeaguesFromUserIDAndSeason(TEST_USER_ID, currentYear)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(List.of(mockLeague))
            .when(om).readValue(eq(mockResponseBody), any(tools.jackson.core.type.TypeReference.class));

        // ACT
        List<SleeperLeagueResponse> result = responseFormatter.getLeaguesFromUserId(TEST_USER_ID);

        // ASSERT
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(sleeperRequestHandler).getLeaguesFromUserIDAndSeason(TEST_USER_ID, currentYear);
    }

    @Test
    void getLeaguesFromUserId_DeserializationError_ReturnsEmptyList() throws Exception {
        // ARRANGE
        int currentYear = LocalDate.now().getYear();
        final String badResponseBody = "Invalid JSON";
        
        when(sleeperRequestHandler.getLeaguesFromUserIDAndSeason(TEST_USER_ID, currentYear)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(badResponseBody);
        
        // Simulate a Jackson error during deserialization
        doThrow(new IOException("Bad JSON format"))
            .when(om).readValue(eq(badResponseBody), any(tools.jackson.core.type.TypeReference.class));

        // ACT
        List<SleeperLeagueResponse> result = responseFormatter.getLeaguesFromUserId(TEST_USER_ID);

        // ASSERT
        assertTrue(result.isEmpty());
    }
    
    // --- Test Cases for getUserIdFromUsername() ---

    @Test
    void getUserIdFromUsername_Success_ReturnsUserId() throws Exception {
        // ARRANGE
        final String mockResponseBody = "{\"user_id\": \"456\"}";
        SleeperUsernameResponse mockUserResponse = new SleeperUsernameResponse();
        mockUserResponse.setUserId(TEST_USER_ID);
        
        when(sleeperRequestHandler.getUserFromUsername(TEST_USERNAME)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(mockUserResponse)
            .when(om).readValue(eq(mockResponseBody), eq(SleeperUsernameResponse.class));

        // ACT
        SleeperUsernameResponse result = responseFormatter.getUserIdFromUsername(TEST_USERNAME);

        // ASSERT
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getUserId());
    }
    
    @Test
    void getUserIdFromUsername_NotFound_ReturnsNull() throws Exception {
        // ARRANGE
        when(sleeperRequestHandler.getUserFromUsername(TEST_USERNAME)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_NOT_FOUND); // 404

        // ACT
        SleeperUsernameResponse result = responseFormatter.getUserIdFromUsername(TEST_USERNAME);

        // ASSERT
        assertNull(result);
    }
    
    // --- Test Cases for getUsersFromLeague() ---

    @Test
    void getUsersFromLeague_Success_ReturnsListOfUsers() throws Exception {
        // ARRANGE
        final String mockResponseBody = "[{\"user_id\": \"1\"}]";
        SleeperUserResponse mockUser = new SleeperUserResponse();
        
        when(sleeperRequestHandler.getUsersFromLeague(TEST_LEAGUE_ID)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(List.of(mockUser))
            .when(om).readValue(eq(mockResponseBody), any(tools.jackson.core.type.TypeReference.class));

        // ACT
        List<SleeperUserResponse> result = responseFormatter.getUsersFromLeague(TEST_LEAGUE_ID);

        // ASSERT
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    // --- Test Cases for getRostersFromLeagueId() ---

    @Test
    void getRostersFromLeagueId_Success_ReturnsListOfRosters() throws Exception {
        // ARRANGE
        final String mockResponseBody = "[{\"roster_id\": 1}]";
        SleeperRosterUserResponse mockRoster = new SleeperRosterUserResponse();
        
        when(sleeperRequestHandler.getRostersFromLeague(TEST_LEAGUE_ID)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(List.of(mockRoster))
            .when(om).readValue(eq(mockResponseBody), any(tools.jackson.core.type.TypeReference.class));

        // ACT
        List<SleeperRosterUserResponse> result = responseFormatter.getRostersFromLeagueId(TEST_LEAGUE_ID);

        // ASSERT
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
    
    // --- Test Cases for getMatchupsFromLeagueIdAndWeek (Blocking) ---

    @Test
    void getMatchupsFromLeagueIdAndWeek_Success_ReturnsListOfMatchups() throws Exception {
        // ARRANGE
        final int testWeek = 5;
        final String mockResponseBody = "[{\"matchup_id\": 1}]";
        SleeperMatchupResponse mockMatchup = new SleeperMatchupResponse();
        
        when(sleeperRequestHandler.getMatchupsFromLeagueIdAndWeek(TEST_LEAGUE_ID, testWeek)).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(List.of(mockMatchup))
            .when(om).readValue(eq(mockResponseBody), any(tools.jackson.core.type.TypeReference.class));

        // ACT
        List<SleeperMatchupResponse> result = responseFormatter.getMatchupsFromLeagueIdAndWeek(TEST_LEAGUE_ID, testWeek);

        // ASSERT
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
    
    // --- Test Cases for getNFLState() ---

    @Test
    void getNFLState_Success_ReturnsNFLStateObject() throws Exception {
        // ARRANGE
        final String mockResponseBody = "{\"week\": 10, \"season\": \"2024\"}";
        SleeperNFLStateResponse mockState = new SleeperNFLStateResponse();
        mockState.setWeek(10);
        
        when(sleeperRequestHandler.getNFLState()).thenReturn(mockHttpResponse);
        when(mockHttpResponse.statusCode()).thenReturn(HttpStatus.SC_OK);
        when(mockHttpResponse.body()).thenReturn(mockResponseBody);

        doReturn(mockState)
            .when(om).readValue(eq(mockResponseBody), eq(SleeperNFLStateResponse.class));

        // ACT
        SleeperNFLStateResponse result = responseFormatter.getNFLState();

        // ASSERT
        assertNotNull(result);
        assertEquals(10, result.getWeek());
    }

    // --- Test Cases for nonBlockGetMatchupsFromLeagueIdAndWeek (Reactive) ---

    // This requires mocking the WebClient's fluent API chain (get().uri().retrieve()...)
    @Test
    void nonBlockGetMatchupsFromLeagueIdAndWeek_Success_ReturnsMonoOfMatchups() {
        // ARRANGE
        final int testWeek = 5;
        final String uri = String.format("/league/%s/matchups/%d", TEST_LEAGUE_ID, testWeek);
        SleeperMatchupResponse mockMatchup = new SleeperMatchupResponse();
        List<SleeperMatchupResponse> expectedList = List.of(mockMatchup);

        // Mock the WebClient chain
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(uri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
        // Mocking the successful bodyToMono call
        // The cast to ParameterizedTypeReference is necessary due to generics/type erasure
        when(responseSpec.bodyToMono(any()))
            .thenReturn(Mono.just(expectedList));

        // ACT
        Mono<List<SleeperMatchupResponse>> resultMono = 
            responseFormatter.nonBlockGetMatchupsFromLeagueIdAndWeek(TEST_LEAGUE_ID, testWeek);

        // ASSERT
        // Block for testing, but the method returns a Mono
        List<SleeperMatchupResponse> result = resultMono.block(); 
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void nonBlockGetMatchupsFromLeagueIdAndWeek_Http404_ReturnsEmptyMono() {
        // ARRANGE
        final int testWeek = 5;
        final String uri = String.format("/league/%s/matchups/%d", TEST_LEAGUE_ID, testWeek);

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        ClientResponse mockClientResponse = mock(ClientResponse.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(uri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Mock the onStatus part for 4xx/5xx error handling
        when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
            // Get the status predicate and handler function from the invocation arguments
            java.util.function.Predicate<org.springframework.http.HttpStatus> predicate = invocation.getArgument(0);
            java.util.function.Function<ClientResponse, Mono<? extends Throwable>> handlerFunction = invocation.getArgument(1);

            // If the status is 4xx or 5xx (predicate returns true), call the handler
            if (predicate.test(org.springframework.http.HttpStatus.NOT_FOUND)) { 
                when(mockClientResponse.statusCode()).thenReturn(org.springframework.http.HttpStatus.NOT_FOUND);
                return handlerFunction.apply(mockClientResponse); // Returns Mono<SleeperException>
            }
            return responseSpec;
        });

        // Mock the remaining bodyToMono part to return the error
        when(responseSpec.bodyToMono(any()))
            .thenReturn(Mono.error(new SleeperException("Simulated error")));

        // ACT
        Mono<List<SleeperMatchupResponse>> resultMono = 
            responseFormatter.nonBlockGetMatchupsFromLeagueIdAndWeek(TEST_LEAGUE_ID, testWeek);

        // ASSERT
        // Verify that the onErrorResume caught the SleeperException and returned an empty list
        List<SleeperMatchupResponse> result = resultMono.block(); 
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Creates a Mockito matcher to verify a TypeReference argument by checking 
     * its generic type information against an expected Type.
     */
    private static <T> ArgumentMatcher<TypeReference<T>> isTypeRefFor(final Type expectedType) {
        return argument -> {
            if (argument == null) {
                return false;
            }
            // Check if the TypeReference's actual type matches the expected Type
            Type actualType = argument.getType();
            return actualType.equals(expectedType);
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> convert(TypeReference<T> ref) {
        return (Class<T>)((ParameterizedType) ref.getType()).getRawType();
    }
}