package com.bwl.apiusers.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        AuthCredentials authCredentials = new AuthCredentials();

        try {
            authCredentials = new ObjectMapper().readValue(request.getReader(), AuthCredentials.class);
            if (authCredentials.getIdApplication() == null) {
                throw new AuthenticationServiceException("Error parsing authentication request");
            }
        } catch (IOException ignored) {
        }

        UserDetailServiceImpl.setIdApplication(authCredentials.getIdApplication());

        UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
                authCredentials.getUsername(),
                authCredentials.getPassword(),
                Collections.emptyList());
        return getAuthenticationManager().authenticate(usernamePAT);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();

        String token = TokenUtils.createToken(
                userDetails.getIdApplication(),
                userDetails.getUsername(),
                userDetails.getAuthorities());

        response.addHeader("Authorization", "Bearer " + token);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", userDetails.getId());
        body.put("username", userDetails.getUsername());
        body.put("name", userDetails.getName());
        body.put("enabled", userDetails.getEnabled());
        body.put("profileKeycodes", userDetails.getProfileKeycodes());
        body.put("profilePermissions", userDetails.getProfilePermissions());
        body.put("token", "Bearer " + token);
        body.put("idApplication", userDetails.getIdApplication());

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("data", body);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(responseBody);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        UserDetailServiceImpl.clearIdApplication();

        super.successfulAuthentication(request, response, chain, authResult);
    }
}
