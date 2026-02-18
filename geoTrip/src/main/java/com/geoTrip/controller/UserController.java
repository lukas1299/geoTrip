package com.geoTrip.controller;

import com.geoTrip.model.UserResponse;
import com.geoTrip.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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
