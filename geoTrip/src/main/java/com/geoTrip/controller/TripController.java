package com.geoTrip.controller;

import com.geoTrip.config.XMLParser;
import com.geoTrip.model.*;
import com.geoTrip.exception.UserNotFoundException;
import com.geoTrip.service.TripService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

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

    @DeleteMapping("/{tripId}")
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<TripResponse> deleteTrip(@PathVariable UUID tripId) {

        return ResponseEntity.ok(tripService.deleteTrip(tripId));
    }

    @PutMapping("/{tripId}/points")
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<TripResponse> addPointToTrip(@PathVariable UUID tripId, @RequestBody PointRequest pointRequest) {
        return ResponseEntity.ok(tripService.addPointToTrip(tripId, pointRequest));
    }

    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<TripResponse> importTrip(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("file") MultipartFile file,
            @RequestParam("tripType") TripType tripType
    ) throws Exception {


        return ResponseEntity.ok(tripService.importTrip(jwt, file, tripType));
    }

    @PostMapping("/loadExampleTrip")
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<List<List<Double>>> loadExampleTrip(@RequestBody SelectedPoints selectedPoints) {
        return ResponseEntity.ok(tripService.generateExampleTrip(selectedPoints));
    }

    @PostMapping("/calculate-between-points")
    @PreAuthorize("hasRole('role_user')")
    public ResponseEntity<List<List<Double>>> calculateRouteBetweenPoints(@RequestBody SelectedPoints selectedPoints) {
        return ResponseEntity.ok(tripService.generateTripBetweenPoints(selectedPoints));
    }

//    @PostMapping("/extend-from-last-point")
//    @PreAuthorize("hasRole('role_user')")
//    public ResponseEntity<List<List<Double>>> extendRouteFromLastPoint(@RequestBody SelectedPoints selectedPoints) {
//        return ResponseEntity.ok(tripService.generateTripBetweenPoints(selectedPoints));
//    }



    //FIXME EXPERIMENTAL ENDPOINT
    @PostMapping("/{tripId}/add-points")
    public ResponseEntity<TripResponse> addPointsToTrip(@PathVariable UUID tripId) {
        return ResponseEntity.ok(tripService.addManyPointToTrip(tripId));
    }
}
