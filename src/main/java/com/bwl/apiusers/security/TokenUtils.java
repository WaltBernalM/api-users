package com.bwl.apiusers.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.crypto.SecretKey;
import java.util.*;

public class TokenUtils {
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_000L;

    public static String createToken(Integer idApplication, String username) {
        long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("idApplication", idApplication);

        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expirationDate)
                .addClaims(extra)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        } catch (JwtException e) {
            return null;
        }
    }
}
