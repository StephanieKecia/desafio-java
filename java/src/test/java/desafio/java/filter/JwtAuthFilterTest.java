package desafio.java.filter;

import desafio.java.service.JwtService;
import desafio.java.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveIgnorarRequisicaoSemHeaderAuthorization() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void deveIgnorarHeaderInvalidoSemBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Token 123");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void deveIgnorarQuandoTokenExpirado() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer expired.jwt.token");
        when(jwtService.extractUsername("expired.jwt.token")).thenThrow(new ExpiredJwtException(null, null, "Token expirado"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void deveAutenticarComTokenValido() throws Exception {
        String token = "valid.jwt.token";
        String username = "user@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);

        User userDetails = new User(username, "password", Collections.emptyList());
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, username)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);

        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(auth);
        assertEquals(username, ((User) auth.getPrincipal()).getUsername());
    }

    @Test
    void naoDeveAutenticarComTokenInvalido() throws Exception {
        String token = "invalid.jwt.token";
        String username = "user@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.isTokenValid(token, username)).thenReturn(false);

        User userDetails = new User(username, "password", Collections.emptyList());
        when(userService.loadUserByUsername(username)).thenReturn(userDetails);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
