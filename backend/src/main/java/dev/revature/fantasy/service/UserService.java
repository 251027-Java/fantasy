package dev.revature.fantasy.service;

import dev.revature.fantasy.model.User;
import dev.revature.fantasy.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepo repo;

    public UserService(UserRepo repo) {
        this.repo = repo;
    }

    @Transactional
    public List<User> idempotentSave(List<User> users) {
        this.repo.batchIdempotentSave(users);
        return users;
    }
}
