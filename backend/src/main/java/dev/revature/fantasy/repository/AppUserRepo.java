package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByEmail(String email);
}
