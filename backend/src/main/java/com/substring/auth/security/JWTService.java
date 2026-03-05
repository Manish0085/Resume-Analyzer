package com.substring.auth.security;

import com.substring.auth.entity.Role;
import com.substring.auth.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
public class JWTService {

    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private final String issuer;

    public JWTService(@Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds) {

        if (secret == null || secret.length() < 64) {
            throw new IllegalArgumentException("Invalid secret");
        }
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.refreshTtlSeconds = refreshTtlSeconds;
        this.issuer = issuer;
        this.accessTtlSeconds = accessTtlSeconds;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getId().toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .addClaims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "typ", "access"))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(User user, String jti) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setId(jti)
                .setSubject(user.getId().toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .addClaims(Map.of(
                        "typ", "refresh"))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // parse token
    public Jws<Claims> parse(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build().parseClaimsJws(token);
        } catch (JwtException e) {
            throw e;
        }
    }

    public boolean isAccessToken(String token) {
        Claims c = parse(token).getBody();
        return "access".equals(c.get("typ"));
    }

    public boolean isRefreshToken(String token) {
        Claims c = parse(token).getBody();
        return "refresh".equals(c.get("typ"));
    }

    public UUID getUserId(String token) {
        Claims c = parse(token).getBody();
        return UUID.fromString(c.getSubject());
    }

    public String getJti(String token) {
        Claims c = parse(token).getBody();
        return c.getId();
    }

    public List<String> getRoles(String token) {
        Claims c = parse(token).getBody();
        return (List<String>) c.get("roles");
    }

    public String getEmail(String token) {
        Claims c = parse(token).getBody();
        return (String) c.get("email");
    }

}
