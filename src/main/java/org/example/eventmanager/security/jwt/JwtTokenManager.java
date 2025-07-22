package org.example.eventmanager.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.eventmanager.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SecretKey secretKey;

    private final Integer expiration;

    public JwtTokenManager(
            @Value("${jwt.secret-key}")String keyString,
            @Value("${jwt.lifetime}")Integer expiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
        this.expiration = expiration;
    }

    public String createJwtToken(User user ) {
        logger.info("Creating jwt token");

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());
        return  Jwts.builder()
                .claims(claims)
                .subject(user.getLogin())
                .signWith(secretKey)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
    }

    public String getLoginFromToken(String token) {
        logger.info("Getting login from token = {}", token);

        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getPayload()
                .getSubject();

    }
}