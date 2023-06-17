package com.geoTrip.controller;

import com.geoTrip.model.Token;
import com.geoTrip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TestController {

    private final UserRepository userRepository;

    @GetMapping("/test")
    public List<Token> test(){

        return userRepository.findAll().get(0).getTokens();
    }

    @GetMapping("/user-endpoint")
    public String user(){
        return "user";
    }

    @GetMapping("/admin-endpoint")
    public String admin(){
        return "admin";
    }

}
