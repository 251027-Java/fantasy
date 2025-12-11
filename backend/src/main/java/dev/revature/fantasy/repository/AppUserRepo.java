package dev.revature.fantasy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.revature.fantasy.model.AppUser;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByEmail(String email);
}
