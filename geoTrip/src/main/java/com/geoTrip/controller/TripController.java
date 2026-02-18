package com.geoTrip.controller;

import com.geoTrip.model.*;
import com.geoTrip.exception.UserNotFoundException;
import com.geoTrip.service.TripService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin
@Slf4j
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;

    @GetMapping
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<List<TripResponse>> getUserTrip(@AuthenticationPrincipal Jwt jwt) throws UserNotFoundException {
        return ResponseEntity.ok(tripService.getUserTrips(jwt));
    }

    @PostMapping
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<TripResponse> createTrip(@AuthenticationPrincipal Jwt jwt, @RequestBody TripRequest tripRequest) throws UserNotFoundException {
        return ResponseEntity.ok(tripService.createTrip(jwt, tripRequest));
    }

//    @PutMapping("/{tripId}/points")
//    public ResponseEntity<TripResponse> addPointToTrip(@PathVariable UUID tripId, @RequestBody PointRequest pointRequest) {
//
//        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new EntityNotFoundException("The tour does not exist."));
//        var tripResponse = tripService.addPointToTrip(trip, pointRequest);
//
//        return ResponseEntity.ok(tripResponse);
//    }
//
//    @PostMapping("/{tripId}/add-points")
//    public ResponseEntity<TripResponse> addPointsToTrip(@PathVariable UUID tripId){
//        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new EntityNotFoundException("The tour does not exist."));
//        var tripResponse = tripService.addManyPointToTrip(trip);
//
//        return ResponseEntity.ok(tripResponse);
//    }
//
//    @DeleteMapping("/{tripId}")
//    public void deleteTrip(@PathVariable UUID tripId) {
//
//        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new EntityNotFoundException("The tour does not exist."));
//        tripService.deleteTrip(trip);
//    }


}
