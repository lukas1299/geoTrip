package com.geoTrip.controller;

import com.geoTrip.model.*;
import com.geoTrip.exception.UserNotFoundException;
import com.geoTrip.repository.TripRepository;
import com.geoTrip.repository.UserRepository;
import com.geoTrip.service.TripService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripService tripService;

    @GetMapping
    public ResponseEntity<List<TripResponse>> getAllTrips() {
        List<TripResponse> tripResponseList = tripRepository.findAll()
                .stream()
                .map(trip -> new TripResponse(trip.getId(), trip.getName(), trip.getPointList()))
                .toList();

        return ResponseEntity.ok(tripResponseList);
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@RequestBody TripRequest tripRequest, Authentication authentication) throws UserNotFoundException {

        User user = userRepository.findByUsernameOrEmail(authentication.getName(), null).orElseThrow(() -> new UserNotFoundException("User does not exists"));
        var tripResponse = tripService.createTrip(user, tripRequest);

        return ResponseEntity.ok(tripResponse);
    }

    @PutMapping("/{tripId}/points")
    public ResponseEntity<TripResponse> addPointToTrip(@PathVariable UUID tripId, @RequestBody PointRequest pointRequest) {

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new EntityNotFoundException("The tour does not exist."));
        var tripResponse = tripService.addPointToTrip(trip, pointRequest);

        return ResponseEntity.ok(tripResponse);
    }

    @DeleteMapping("/{tripId}")
    public void deleteTrip(@PathVariable UUID tripId) {

        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new EntityNotFoundException("The tour does not exist."));
        tripService.deleteTrip(trip);
    }
}
