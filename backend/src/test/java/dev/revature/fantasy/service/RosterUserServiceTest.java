package dev.revature.fantasy.service;

import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.repository.LeagueRepo;
import dev.revature.fantasy.repository.RosterUserRepo;
import dev.revature.fantasy.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RosterUserServiceTest {
    @Mock
    RosterUserRepo rosterUserRepo;

    @Mock
    UserRepo userRepo;

    @Mock
    LeagueRepo leagueRepo;

    @InjectMocks
    RosterUserService service;

    @Test
    void obtainRosterUserWithValidUserIdAndLeagueId_returnsFoundRosterUser() {
        String leagueId = "asd";
        String userId = "qwe";

        RosterUser rosterUser = new RosterUser();
        Optional<RosterUser> mockRosterUser = Optional.of(rosterUser);

        when(rosterUserRepo.findByUserIdAndLeagueId(userId, leagueId)).thenReturn(mockRosterUser);

        Optional<RosterUser> realRosterUser = service.getRosterUserByUserIdAndLeagueId(userId, leagueId);

        assertEquals(mockRosterUser, realRosterUser);
    }
}
