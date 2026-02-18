package com.geoTrip.controller;

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


    @GetMapping("/user-endpoint")
    public String user(){
        return "user";
    }

    @GetMapping("/admin-endpoint")
    public String admin(){
        return "admin";
    }

}
