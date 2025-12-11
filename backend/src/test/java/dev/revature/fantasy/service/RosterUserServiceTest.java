package dev.revature.fantasy.service;

import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.repository.LeagueRepo;
import dev.revature.fantasy.repository.RosterUserRepo;
import dev.revature.fantasy.repository.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
    void testObtainRosterUsersFromLeagueId() {
        String leagueId = "asd";
        List<RosterUser> mockRosterUsers = List.of();

when(rosterUserRepo.findRosterUsersByLeagueId(leagueId)).thenReturn(mockRosterUsers);

List<RosterUser> rosterUsers= service.getAllRosterUsersByLeagueId(leagueId);

        Assertions.assertEquals(1,rosterUsers.size());
    }
}
