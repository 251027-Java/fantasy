package dev.revature.fantasy.repo;

import java.util.List;

import dev.revature.fantasy.model.User;

public interface UserRepoCustom {
    public int[] batchIdempotentSave(List<User> users);
}
