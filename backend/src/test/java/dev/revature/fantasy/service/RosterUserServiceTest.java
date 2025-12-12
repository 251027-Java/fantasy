package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import dev.revature.fantasy.dto.RosterUserDto;
import dev.revature.fantasy.model.League;
import dev.revature.fantasy.model.RosterUser;
import dev.revature.fantasy.model.User;
import dev.revature.fantasy.repository.LeagueRepo;
import dev.revature.fantasy.repository.RosterUserRepo;
import dev.revature.fantasy.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    void getRosterUserByUserIdAndLeagueId_validUserIdAndLeagueId_returnsFoundRosterUser() {
        String leagueId = "asd";
        String userId = "qwe";

        RosterUser rosterUser = new RosterUser();
        Optional<RosterUser> mockRosterUser = Optional.of(rosterUser);

        when(rosterUserRepo.findByUserIdAndLeagueId(userId, leagueId)).thenReturn(mockRosterUser);

        Optional<RosterUser> realRosterUser = service.getRosterUserByUserIdAndLeagueId(userId, leagueId);

        assertEquals(mockRosterUser, realRosterUser);
    }

    @Test
    void getAllRosterUsersByLeagueId_validLeagueId_returnsAllRosterUsersFound() {
        String leagueId = "asd";
        List<RosterUser> mockRosterUsers = List.of(new RosterUser());

        when(rosterUserRepo.findRosterUsersByLeagueId(leagueId)).thenReturn(mockRosterUsers);

        List<RosterUser> realRosterUsers = service.getAllRosterUsersByLeagueId(leagueId);

        assertEquals(mockRosterUsers, realRosterUsers);
    }

    @Test
    void getRosterUserByRosterIdAndLeagueId_validRosterIdAndLeagueId_returnsFoundRosterUser() {
        int rosterId = 1;
        String leagueId = "asd";

        Optional<RosterUser> mockRosterUser = Optional.of(new RosterUser());
        when(rosterUserRepo.findByRosterIdAndLeagueId(rosterId, leagueId)).thenReturn(mockRosterUser);

        Optional<RosterUser> realRosterUser = service.getRosterUserByRosterIdAndLeagueId(rosterId, leagueId);

        assertEquals(mockRosterUser, realRosterUser);
    }

    @Test
    void batchUpsert_upsertRosterUserDtos_returnsRosterUsersModelsEquivalent() {
        String userId = "asd";
        String leagueId = "qwe";
        RosterUserDto mockDto = new RosterUserDto(1, userId, leagueId, 2, 3, 4, 5, 6, 7, 8);

        when(userRepo.getReferenceById(userId)).thenReturn(new User());
        when(leagueRepo.getReferenceById(leagueId)).thenReturn(new League());
        when(rosterUserRepo.batchUpsert(anyList())).thenAnswer(e -> e.getArgument(0));

        List<RosterUser> realRosterUsers = service.upsertUsers(List.of(mockDto));

        assertEquals(1, realRosterUsers.size());

        RosterUser realRosterUser = realRosterUsers.getFirst();

        assertEquals(mockDto.rosterId(), realRosterUser.getRosterId());
        assertNotNull(realRosterUser.getUser());
        assertNotNull(realRosterUser.getLeague());
        assertEquals(mockDto.wins(), realRosterUser.getWins());
        assertEquals(mockDto.ties(), realRosterUser.getTies());
        assertEquals(mockDto.losses(), realRosterUser.getLosses());
        assertEquals(mockDto.fptsDecimal(), realRosterUser.getFptsDecimal());
        assertEquals(mockDto.fptsAgainstDecimal(), realRosterUser.getFptsAgainstDecimal());
        assertEquals(mockDto.fptsAgainst(), realRosterUser.getFptsAgainst());
        assertEquals(mockDto.fpts(), realRosterUser.getFpts());
    }

    @Test
    void getRosterUserIdToName_validRosterUsers_mappingOfRosterIdsToUserDisplayName() {
        User mockUser = new User();
        mockUser.setDisplayName("asd");

        RosterUser mockRosterUser = new RosterUser();
        mockRosterUser.setId(5L);
        mockRosterUser.setUser(mockUser);

        Map<Long, String> mapping = service.getRosterUserIdToName(List.of(mockRosterUser));

        assertEquals(1, mapping.size());
        assertEquals("asd", mapping.get(5L));
    }
}
