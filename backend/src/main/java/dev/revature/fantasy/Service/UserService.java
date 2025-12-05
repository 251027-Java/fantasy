package dev.revature.fantasy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.revature.fantasy.model.User;
import dev.revature.fantasy.repo.UserRepo;
import jakarta.transaction.Transactional;

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
