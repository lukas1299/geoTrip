package com.geoTrip.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoTrip.model.Token;
import com.geoTrip.model.User;
import com.geoTrip.exception.UserNotFoundException;
import com.geoTrip.repository.TokenRepository;
import com.geoTrip.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private static final int FIFTEEN_MIN_AD_MILLIS = 15 * 60 * 1000;
    private static final int SIXTY_MIN_AD_MILLIS = 60 * 60 * 1000;

    @Transactional
    public JwtResponse authenticate(JwtRequest jwtRequest) throws UserNotFoundException {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.login(), jwtRequest.password()));

        var access_token = jwtTokenUtil.generateToken(authentication.getName(), FIFTEEN_MIN_AD_MILLIS);
        var refresh_token = jwtTokenUtil.generateToken(authentication.getName(), SIXTY_MIN_AD_MILLIS);

        var user = userRepository.findByUsernameOrEmail(jwtRequest.login(), null).orElseThrow(() -> new UserNotFoundException("User does not exists"));
        var createdToken = tokenRepository.save(createToken(access_token));

        List<Token> userTokens = Optional.ofNullable(user.getTokens())
                .orElse(new ArrayList<>());

        userTokens.add(createdToken);
        user.setTokens(userTokens);
        userRepository.save(user);
        return new JwtResponse(access_token, refresh_token);
    }

    @Transactional
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws UserNotFoundException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;

        if (header == null || !header.startsWith("Bearer ")) {
            return;
        }

        refreshToken = header.substring(7);
        username = jwtTokenUtil.getUsernameFromToken(refreshToken);

        if (username != null) {
            var user = userRepository.findByUsernameOrEmail(username, null).orElseThrow(() -> new UserNotFoundException("User does not exists"));

            var accessToken = jwtTokenUtil.generateToken(user.getUsername(), FIFTEEN_MIN_AD_MILLIS);

            revokeUserTokens(user);

            List<Token> userTokens = Optional.ofNullable(user.getTokens())
                    .orElse(new ArrayList<>());

            var createdToken = createToken(accessToken);

            userTokens.add(createdToken);
            user.setTokens(userTokens);

            tokenRepository.save(createdToken);
            userRepository.save(user);

            var authResponse = new JwtResponse(accessToken, refreshToken);
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        }
    }

    private Token createToken(String access_token) {
        return Token
                .builder()
                .id(UUID.randomUUID())
                .token(access_token)
                .expired(false)
                .revoked(false)
                .build();
    }

    private void revokeUserTokens(User user) {

        var tokens = Optional.ofNullable(user.getTokens())
                .orElse(new ArrayList<>());

        tokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
    }
}
