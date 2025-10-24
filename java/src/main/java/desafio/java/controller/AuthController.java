package desafio.java.controller;

import desafio.java.service.JwtService;
import desafio.java.service.UserService;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails user = userService.loadUserByUsername(username);
        String role = user.getAuthorities().iterator().next().getAuthority();
        String token = jwtService.generateToken(username, role);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", role);
        return response;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Token ausente ou inválido");
            return ResponseEntity.badRequest().body(response);
        }

        String token = authHeader.substring(7);
        try {
            String username = jwtService.extractUsername(token);
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    userService.loadUserByUsername(username);

            String role = "ROLE_USER";
            if (!userDetails.getAuthorities().isEmpty()) {
                role = userDetails.getAuthorities().iterator().next().getAuthority();
            }

            String newToken = jwtService.refreshToken(token);
            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            response.put("role", role);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Token inválido ou não pode ser renovado");
            return ResponseEntity.status(401).body(response);
        }
    }

}
