package com.geoTrip.service;

import com.geoTrip.model.User;
import com.geoTrip.model.UserRequest;
import com.geoTrip.exception.UserAlreadyExistsException;
import com.geoTrip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserRequest userRequest) throws Exception {

        if(userRepository.findByUsernameOrEmail(userRequest.username(), userRequest.email()).isPresent()){
            throw new UserAlreadyExistsException("User currently exist");
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(userRequest.username())
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password()))
                .role(userRequest.role())
                .build();

        return userRepository.save(user);
    }

}
