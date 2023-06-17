package com.geoTrip.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional.ofNullable(request.getHeader("Authorization"))
                    .filter(requestTokenHeader -> requestTokenHeader.startsWith("Bearer "))
                    .map(requestTokenHeader -> requestTokenHeader.substring(7))
                    .filter(token -> !jwtTokenUtil.isTokenExpired(token) && jwtTokenUtil.isTokenValid(token))
                    .map(jwtTokenUtil::getUsernameFromToken)
                    .map(jwtUserDetailsService::loadUserByUsername)
                    .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()))
                    .ifPresent(handleAuthToken(request));
        }
        filterChain.doFilter(request, response);
    }

    private Consumer<UsernamePasswordAuthenticationToken> handleAuthToken(HttpServletRequest request) {
        return authToken -> {
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        };
    }
}
