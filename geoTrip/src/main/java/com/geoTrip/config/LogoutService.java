package com.geoTrip.config;


import com.geoTrip.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        var authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
            return;
        }

        var token = tokenRepository.findByToken(authenticationHeader.substring(7)).orElseThrow();

        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);

        SecurityContextHolder.clearContext();
    }
}
