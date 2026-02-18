package com.geoTrip.controller;

import com.geoTrip.model.User;
import com.geoTrip.model.UserResponse;
import com.geoTrip.repository.UserRepository;
import com.geoTrip.service.UserService;
import com.nimbusds.jwt.JWTClaimNames;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('role_user')")
    public UserResponse getUser(@AuthenticationPrincipal Jwt jwt) {
        return userService.getUser(jwt);
    }
}
