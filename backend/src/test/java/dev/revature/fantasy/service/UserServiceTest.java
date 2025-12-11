package dev.revature.fantasy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import dev.revature.fantasy.model.User;
import dev.revature.fantasy.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepo repo;

    @InjectMocks
    UserService service;

    @Test
    void testReturnsSameContentOnSave() {
        User user = new User("an id", "a display name");
        List<User> mockUsers = List.of(user);

        List<User> users = service.idempotentSave(mockUsers);

        assertEquals(1, users.size());

        User realUser = users.getFirst();

        assertEquals("an id", realUser.getId());
        assertEquals("a display name", realUser.getDisplayName());

        verify(repo, times(1)).saveAll(mockUsers);
    }
}
