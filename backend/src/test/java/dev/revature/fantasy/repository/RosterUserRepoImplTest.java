package dev.revature.fantasy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.revature.fantasy.model.RosterUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class RosterUserRepoImplTest {
    @Mock
    JdbcTemplate jdbcTemplate;

    @InjectMocks
    RosterUserRepoImpl rosterUserRepo;

    @Test
    void batchUpsert_upsertingRosterUsers_returnsTheSameRosterUsers() {
        List<RosterUser> mockRosterUsers = List.of(new RosterUser());

        when(jdbcTemplate.batchUpdate(
                        any(PreparedStatementCreator.class),
                        any(BatchPreparedStatementSetter.class),
                        any(KeyHolder.class)))
                .then(e -> {
                    // keyholder would normally get updated during the batchUpdate call, so mimic that
                    KeyHolder keyHolder = e.getArgument(2);
                    keyHolder.getKeyList().add(Map.of("id", 1L));

                    return new int[] {1};
                });

        List<RosterUser> realRosterUsers = rosterUserRepo.batchUpsert(mockRosterUsers);

        assertEquals(mockRosterUsers, realRosterUsers);
        verify(jdbcTemplate, times(1))
                .batchUpdate(
                        any(PreparedStatementCreator.class),
                        any(BatchPreparedStatementSetter.class),
                        any(KeyHolder.class));
    }
}
