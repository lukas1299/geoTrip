package com.geoTrip.service;

import com.geoTrip.exception.UserNotFoundException;
import com.geoTrip.model.User;
import com.geoTrip.model.UserResponse;
import com.geoTrip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUser(Jwt jwt){
        return userMapper(userRepository.findById(UUID.fromString(jwt.getClaim(JwtClaimNames.SUB))).orElseThrow());
    }

    private static UserResponse userMapper(User user){
        return new UserResponse(
                user.getUsername(),
                user.getEmail());
    }
}
