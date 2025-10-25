package desafio.java.controller;

import desafio.java.service.JwtService;
import desafio.java.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private User mockUser;

    @Before
    public void setUp() {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        mockUser = new User("testuser", "password", authorities);
    }

    @Test
    public void testLogin() {
        Mockito.when(userService.loadUserByUsername(anyString())).thenReturn(mockUser);
        Mockito.when(jwtService.generateToken(anyString(), anyString())).thenReturn("mock-token");

        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "testuser");
        loginData.put("password", "password");

        Map<String, String> response = authController.login(loginData);

        assertEquals("mock-token", response.get("token"));
        assertEquals("ROLE_USER", response.get("role"));

        Mockito.verify(userService).loadUserByUsername("testuser");
        Mockito.verify(jwtService).generateToken("testuser", "ROLE_USER");
    }

    @Test
    public void testRefreshToken_Valid() {
        String oldToken = "Bearer old-token";
        Mockito.when(jwtService.extractUsername("old-token")).thenReturn("testuser");
        Mockito.when(userService.loadUserByUsername("testuser")).thenReturn(mockUser);
        Mockito.when(jwtService.refreshToken("old-token")).thenReturn("new-token");

        ResponseEntity<?> responseEntity = authController.refreshToken(oldToken);

        assertEquals(200, responseEntity.getStatusCodeValue());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) responseEntity.getBody();
        assertEquals("new-token", body.get("token"));
        assertEquals("ROLE_USER", body.get("role"));
    }

    @Test
    public void testRefreshToken_InvalidHeader() {
        String invalidHeader = "InvalidHeader";

        ResponseEntity<?> responseEntity = authController.refreshToken(invalidHeader);

        assertEquals(400, responseEntity.getStatusCodeValue());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) responseEntity.getBody();
        assertEquals("Token ausente ou inválido", body.get("error"));
    }

    @Test
    public void testRefreshToken_Exception() throws Exception {
        String oldToken = "Bearer old-token";

        Mockito.when(jwtService.extractUsername("old-token")).thenThrow(new RuntimeException("Invalid token"));

        ResponseEntity<?> responseEntity = authController.refreshToken(oldToken);

        assertEquals(401, responseEntity.getStatusCodeValue());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) responseEntity.getBody();
        assertEquals("Token inválido ou não pode ser renovado", body.get("error"));
    }
}
