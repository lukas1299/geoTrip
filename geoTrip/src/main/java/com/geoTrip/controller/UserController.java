package com.geoTrip.controller;

import com.geoTrip.model.User;
import com.geoTrip.model.UserResponse;
import com.geoTrip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<UserResponse> insertUser(@RequestBody User user) {
        var u = userRepository.save(user);
        return ResponseEntity.ok(new UserResponse(u));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .toList());
    }

}
