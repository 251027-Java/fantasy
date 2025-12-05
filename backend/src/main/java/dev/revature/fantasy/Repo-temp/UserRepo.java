package dev.revature.fantasy.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.revature.fantasy.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, String>, UserRepoCustom {
    
}
