package dev.revature.fantasy.repository;

import dev.revature.fantasy.model.User;

import java.util.List;

public interface UserRepoCustom {
    public int[] batchIdempotentSave(List<User> users);
}
