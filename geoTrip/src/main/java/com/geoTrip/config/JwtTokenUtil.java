package com.geoTrip.config;

import com.geoTrip.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final Environment environment;
    private final TokenRepository tokenRepository;

    private Claims getAllClaimsFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(Objects.requireNonNull(environment.getProperty("SECRET_KEY")).getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String generateToken(String username, long expirationTime) {

        Date tokenExpirationTime = new Date(System.currentTimeMillis() + expirationTime);

        return Jwts.builder().setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(tokenExpirationTime)
                .signWith(SignatureAlgorithm.HS256, Objects.requireNonNull(environment.getProperty("SECRET_KEY")).getBytes()).compact();
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public boolean isTokenValid(String token) {
        var t = tokenRepository.findByToken(token).orElseThrow();
        return !t.isExpired() && !t.isRevoked();
    }
}
