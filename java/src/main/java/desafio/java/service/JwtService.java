package desafio.java.service;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // ⚠️ Use uma chave mais forte e segura (idealmente via variável de ambiente)
    private static final String SECRET_KEY = "chaveSuperSecreta@2025!#jwt";

    // Tempo de expiração — aqui definido para 24h
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    /**
     * Extrai o username (subject) do token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai um claim específico.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Faz o parsing completo do token JWT e retorna todos os claims.
     * Lança ExpiredJwtException se o token estiver expirado.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Aqui você pode logar o evento ou lançar novamente se quiser tratar no filtro
            throw e;
        } catch (JwtException e) {
            throw new RuntimeException("Token inválido", e);
        }
    }

    /**
     * Gera um novo token com username e role.
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Verifica se o token é válido e se pertence ao usuário informado.
     */
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Verifica se o token expirou.
     */
    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 🔄 Gera um novo token a partir de um token válido ou expirado (refresh).
     */
    public String refreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return createToken(claims, claims.getSubject());
        } catch (ExpiredJwtException e) {
            // Permite refresh mesmo se expirado, usando os claims antigos
            Claims claims = e.getClaims();
            return createToken(claims, claims.getSubject());
        }
    }
}
