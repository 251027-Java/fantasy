package dev.revature.fantasy.service;

import dev.revature.fantasy.model.User;
import dev.revature.fantasy.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepo repo;

    @InjectMocks
    UserService service;

    @Test
    void savingUsers_returnsSameUsers() {
        List<User> mockUsers = List.of(new User());

        List<User> users = service.idempotentSave(mockUsers);

        assertEquals(mockUsers,users);
        verify(repo, times(1)).saveAll(mockUsers);
    }
}
