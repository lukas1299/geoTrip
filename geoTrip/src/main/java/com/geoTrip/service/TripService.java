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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    private static final double EARTH_RADIUS = 6371;

    @Transactional
    public void deleteTrip(Trip trip) {
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

        String distance = String.valueOf(calculateDistance(trip)).substring(0, 4);

        return new TripResponse(savedTrip.getId(), savedTrip.getName(), distance, savedTrip.getPointList());
    }

    public TripResponse addPointToTrip(Trip trip, PointRequest pointRequest) {
        Point point = new Point(UUID.randomUUID(), pointRequest.latitude(), pointRequest.longitude(), String.valueOf(LocalDateTime.now()), trip);

        var points = Optional.ofNullable(trip.getPointList()).orElseGet(ArrayList::new);
        points.add(point);
        trip.setPointList(points);

        pointRepository.save(point);
        tripRepository.save(trip);

        String distance = String.valueOf(calculateDistance(trip)).substring(0, 4);

        return new TripResponse(trip.getId(), trip.getName(), distance, points);
    }

    //TODO to refactor
    public TripResponse addManyPointToTrip(Trip trip) {

        var points = List.of(
                new Point(UUID.randomUUID(), 50.094444, 21.483333, "TIME", trip),
                new Point(UUID.randomUUID(), 50.093611, 21.479444, "TIME", trip),
                new Point(UUID.randomUUID(), 50.091944, 21.476944, "TIME", trip),
                new Point(UUID.randomUUID(), 50.091667, 21.476944, "TIME", trip),
                new Point(UUID.randomUUID(), 50.089444, 21.477222, "TIME", trip),
                new Point(UUID.randomUUID(), 50.089167, 21.477222, "TIME", trip),
                new Point(UUID.randomUUID(), 50.086944, 21.477500, "TIME", trip),
                new Point(UUID.randomUUID(), 50.085278, 21.477500, "TIME", trip),
                new Point(UUID.randomUUID(), 50.083333, 21.478056, "TIME", trip),
                new Point(UUID.randomUUID(), 50.081944, 21.478056, "TIME", trip),
                new Point(UUID.randomUUID(), 50.082222, 21.478889, "TIME", trip),
                new Point(UUID.randomUUID(), 50.082222, 21.479444, "TIME", trip),
                new Point(UUID.randomUUID(), 50.082222, 21.479722, "TIME", trip),
                new Point(UUID.randomUUID(), 50.082500, 21.480278, "TIME", trip),
                new Point(UUID.randomUUID(), 50.083056, 21.481389, "TIME", trip),
                new Point(UUID.randomUUID(), 50.083611, 21.483333, "TIME", trip),
                new Point(UUID.randomUUID(), 50.084167, 21.483889, "TIME", trip),
                new Point(UUID.randomUUID(), 50.085278, 21.484722, "TIME", trip),
                new Point(UUID.randomUUID(), 50.087778, 21.484722, "TIME", trip),
                new Point(UUID.randomUUID(), 50.088056, 21.485000, "TIME", trip),
                new Point(UUID.randomUUID(), 50.090833, 21.485000, "TIME", trip),
                new Point(UUID.randomUUID(), 50.093889, 21.484444, "TIME", trip),
                new Point(UUID.randomUUID(), 50.094444, 21.483889, "TIME", trip),
                new Point(UUID.randomUUID(), 50.094722, 21.483611, "TIME", trip));

        trip.setPointList(points);
        pointRepository.saveAll(points);
        tripRepository.save(trip);

        String distance = String.valueOf(calculateDistance(trip)).substring(0, 4);

        return new TripResponse(trip.getId(), trip.getName(), distance, trip.getPointList());
    }

    public double calculateDistance(Trip trip) {
        List<Point> points = trip.getPointList();

        double sum = 0;
        Point previousPoint = null;

        for (Point currentPoint : points) {
            if (previousPoint != null) {
                sum += distance(previousPoint, currentPoint);
            }
            previousPoint = currentPoint;
        }
        return sum;
    }

    private double distance(Point previousPoint, Point currentPoint) {
        double lat1Rad = Math.toRadians(previousPoint.getLatitude());
        double lon1Rad = Math.toRadians(previousPoint.getLongitude());
        double lat2Rad = Math.toRadians(currentPoint.getLatitude());
        double lon2Rad = Math.toRadians(currentPoint.getLongitude());

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
