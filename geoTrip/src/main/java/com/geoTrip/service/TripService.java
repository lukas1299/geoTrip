package com.geoTrip.service;

import com.geoTrip.model.*;
import com.geoTrip.repository.PointRepository;
import com.geoTrip.repository.TripRepository;
import com.geoTrip.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    @Transactional
    public void deleteTrip(Trip trip){
        pointRepository.deleteByTrip(trip);
        tripRepository.delete(trip);
    }

    public TripResponse createTrip(User user, TripRequest tripRequest) {
        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .name(tripRequest.name())
                .startTime("startTime")
                .endTime("EndTime")
                .build();

        var trips = Optional.ofNullable(user.getTrips()).orElseGet(ArrayList::new);

        trips.add(trip);
        user.setTrips(trips);

        trip.setUser(user);
        userRepository.save(user);
        var savedTrip = tripRepository.save(trip);

        return new TripResponse(savedTrip.getId(), savedTrip.getName(), savedTrip.getPointList());
    }

    public TripResponse addPointToTrip(Trip trip, PointRequest pointRequest) {
        Point point = new Point(UUID.randomUUID(), pointRequest.latitude(), pointRequest.longitude(), String.valueOf(LocalDateTime.now()), trip);

        var points = Optional.ofNullable(trip.getPointList()).orElseGet(ArrayList::new);
        points.add(point);
        trip.setPointList(points);

        pointRepository.save(point);
        tripRepository.save(trip);

        return new TripResponse(trip.getId(), trip.getName(), points);
    }
}
