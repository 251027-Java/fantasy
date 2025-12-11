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

import java.util.List;
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
    void obtainRosterUserFromValidUserIdAndLeagueId_returnsFoundRosterUser() {
        String leagueId = "asd";
        String userId = "qwe";

        RosterUser rosterUser = new RosterUser();
        Optional<RosterUser> mockRosterUser = Optional.of(rosterUser);

        when(rosterUserRepo.findByUserIdAndLeagueId(userId, leagueId)).thenReturn(mockRosterUser);

        Optional<RosterUser> realRosterUser = service.getRosterUserByUserIdAndLeagueId(userId, leagueId);

        assertEquals(mockRosterUser, realRosterUser);
    }

    @Test
    void obtainRosterUsersFromValidLeagueId_returnsAllRosterUsersFound() {
        String leagueId = "asd";
        List<RosterUser> mockRosterUsers = List.of(new RosterUser());

        when(rosterUserRepo.findRosterUsersByLeagueId(leagueId)).thenReturn(mockRosterUsers);

        List<RosterUser> realRosterUsers = service.getAllRosterUsersByLeagueId(leagueId);

        assertEquals(mockRosterUsers, realRosterUsers);
    }

    @Test
    void obtainRosterUserFromRosterIdAndLeagueId_returnsFoundRosterUser() {
        int rosterId = 1;
        String leagueId = "asd";

        Optional<RosterUser> mockRosterUser = Optional.of(new RosterUser());
        when(rosterUserRepo.findByRosterIdAndLeagueId(rosterId, leagueId)).thenReturn(mockRosterUser);

        Optional<RosterUser> realRosterUser = service.getRosterUserByRosterIdAndLeagueId(rosterId, leagueId);

        assertEquals(mockRosterUser, realRosterUser);
    }

    @Test
    void upsertRosterUserDtos_returnsRosterUsersModelsEquivalent() {}
}
