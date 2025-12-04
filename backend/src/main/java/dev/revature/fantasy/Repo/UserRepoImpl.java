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

    public UserRepoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public int[] batchIdempotentSave(List<User> users) {
        
        final String sql = """
            INSERT INTO "users" (user_id_num, display_name)
            VALUES (?, ?)
            ON CONFLICT (user_id_num) DO NOTHING 
            """;
        
        List<Object[]> batchArgs = users.stream()
            .map(user -> new Object[] {
                user.getUserId(),  
                user.getDisplayName(),
            })
            .toList();

        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
