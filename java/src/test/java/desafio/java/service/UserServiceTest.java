package desafio.java.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void testLoadUserByUsername_Admin() {
        UserDetails userDetails = userService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        assertNotNull(userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_User() {
        UserDetails userDetails = userService.loadUserByUsername("user");

        assertNotNull(userDetails);
        assertEquals("user", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
        assertNotNull(userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent");
        });
    }
}

