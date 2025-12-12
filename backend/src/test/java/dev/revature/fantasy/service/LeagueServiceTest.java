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
    void idempotentSave_savingLeagues_returnsSameLeagues() {
        List<League> mockLeagues = List.of(new League(), new League());

        List<League> leagues = service.idempotentSave(mockLeagues);

        assertEquals(mockLeagues, leagues);
        verify(repo, times(1)).saveAll(mockLeagues);
    }

    @Test
    void getSizeOfLeague_sizeOfExistingLeague_returnsSizeOfLeague() {
        League mockLeague = new League();
        mockLeague.setNumRosters(10);

        Optional<League> mockFoundLeague = Optional.of(mockLeague);

        String id = "asd";
        when(repo.findById(id)).thenReturn(mockFoundLeague);

        int size = service.getSizeOfLeague(id);

        assertEquals(10, size);
    }

    @Test
    void getSizeOfLeague_sizeOfNonExistantLeague_returnsDefaultValue() {
        String id = "asd";
        when(repo.findById(id)).thenReturn(Optional.empty());

        int size = service.getSizeOfLeague(id);

        assertEquals(-1, size);
    }
}
