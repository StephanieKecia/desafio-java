package desafio.java.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    private final Map<String, UserDetails> users = new HashMap<>();


    public UserService() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        users.put("admin", User.withUsername("admin")
                .password(encoder.encode("123qwe!@#"))
                .roles("ADMIN")
                .build());

        users.put("user", User.withUsername("user")
                .password(encoder.encode("123qwe123"))
                .roles("USER")
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = users.get(username);
        if (user == null) throw new UsernameNotFoundException("Usuário não encontrado");

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))
                .build();
    }

}
