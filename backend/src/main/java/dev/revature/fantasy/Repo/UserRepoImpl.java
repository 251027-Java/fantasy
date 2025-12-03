package dev.revature.fantasy.repo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dev.revature.fantasy.model.User;
import jakarta.transaction.Transactional;

@Repository
public class UserRepoImpl implements UserRepoCustom {
    
    private final JdbcTemplate jdbcTemplate;

    // Inject JdbcTemplate using constructor injection
    public UserRepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional // Ensure the entire batch runs as a single database transaction
    public int[] batchIdempotentSave(List<User> users) {
        
        // --- NATIVE SQL INSERT ... ON CONFLICT (UPSERT) ---
        // The unique constraint used for conflict detection is 'user_id' (the primary key).
        // If a conflict occurs, we update the existing row's display_name and email 
        // using the values from the incoming (EXCLUDED) row.
        final String sql = """
            INSERT INTO "users" (user_id_num, display_name)
            VALUES (?, ?)
            ON CONFLICT (user_id_num) DO NOTHING 
            """;
        
        // 1. Prepare the batch arguments from the list of User entities
        List<Object[]> batchArgs = users.stream()
            .map(user -> new Object[] {
                user.getUserId(),  
                user.getDisplayName(),
            })
            .toList();

        // 2. Execute the single batch update command.
        // This is a single communication to the database, achieving true batching.
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
