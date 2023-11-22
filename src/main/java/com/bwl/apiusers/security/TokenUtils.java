package com.bwl.apiusers.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

public class TokenUtils {
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_000L;

    public static String createToken(
            Integer idApplication,
            String username,
            Collection<? extends GrantedAuthority> authorities) {

        long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("idApplication", idApplication);
        extra.put("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

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
            Collection<? extends GrantedAuthority> authorities = getAuthoritiesFromClaims(claims);
            System.out.println("authoritiesInToken: " + authorities);

            return new UsernamePasswordAuthenticationToken(username, null, authorities);
        } catch (JwtException e) {
            return null;
        }
    }

    private  static Collection<? extends GrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        Object authoritiesClaim = claims.get("authorities");

        if (authoritiesClaim instanceof List) {
            List<?> authoritiesList = (List<?>) authoritiesClaim;
            return authoritiesList.stream()
                    .map(Object::toString)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}

