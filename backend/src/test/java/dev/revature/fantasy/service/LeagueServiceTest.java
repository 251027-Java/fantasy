package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import dev.revature.fantasy.model.League;
import dev.revature.fantasy.repository.LeagueRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LeagueServiceTest {
    @Mock
    LeagueRepo repo;

    @InjectMocks
    LeagueService service;

    @Test
    void testReturnsSameContentOnSave() {
        League l1 = new League("asd", 3, "other name", 6);
        League l2 = new League("id", 4, "name", 1);
        List<League> mockLeagues = List.of(l1, l2);

        List<League> leagues = service.idempotentSave(mockLeagues);
        League realL1 = leagues.get(0);
        League realL2 = leagues.get(1);

        assertEquals(2, leagues.size());

        assertEquals("asd", realL1.getId());
        assertEquals(3, realL1.getNumRosters());
        assertEquals("other name", realL1.getName());
        assertEquals(6, realL1.getSeasonYear());

        assertEquals("id", realL2.getId());
        assertEquals(4, realL2.getNumRosters());
        assertEquals("name", realL2.getName());
        assertEquals(1, realL2.getSeasonYear());

        verify(repo, times(1)).saveAll(mockLeagues);
    }

    @Test
    void testSizeOnMatchingLeagueId() {
        String id = "asd";
        League mockLeague = new League(id, 10, "name", 1);
        Optional<League> mockFoundLeague = Optional.of(mockLeague);

        when(repo.findById(id)).thenReturn(mockFoundLeague);

        int size = service.getSizeOfLeague(id);

        assertEquals(10, size);
    }

    @Test
    void testSizeOnNonMatchingLeagueId() {
        String id = "asd";
        when(repo.findById(id)).thenReturn(Optional.empty());

        int size = service.getSizeOfLeague(id);

        assertEquals(-1, size);
    }
}
