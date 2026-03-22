package com.geoTrip.service;

import com.geoTrip.config.XMLParser;
import com.geoTrip.exception.UserNotFoundException;
import com.geoTrip.model.*;
import com.geoTrip.repository.PointRepository;
import com.geoTrip.repository.TripRepository;
import com.geoTrip.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final XMLParser xmlParser;


    private static final double EARTH_RADIUS = 6371;

    public TripResponse deleteTrip(UUID tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new EntityNotFoundException("The tour does not exist."));
        pointRepository.deleteAll(trip.getPointList());
        tripRepository.delete(trip);

        return mapToTripResponse(trip);
    }

    public TripResponse createTrip(Jwt jwt, TripRequest tripRequest) throws UserNotFoundException {
        User user = userRepository.findById(UUID.fromString(jwt.getClaim(JwtClaimNames.SUB))).orElseThrow(() -> new UserNotFoundException("User does not exists"));
        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .name(tripRequest.name())
                .startTime(null)
                .endTime(null)
                .tripType(tripRequest.tripType())
                .tripStatus(TripStatus.OPEN)
                .pulse(tripRequest.pulse())
                .rate(null)
                .totalTime(null)
                .calorie(tripRequest.calorie())
                .strength(tripRequest.strength())
                .pointList(tripRequest.points().isEmpty() ? new ArrayList<>() : tripRequest.points())
                .distance(tripRequest.points().isEmpty() ? 0.0 : calculateDistance(tripRequest.points().stream().sorted(Comparator.comparing(Point::getTime)).toList()))
                .build();

        var trips = Optional.ofNullable(user.getTrips()).orElseGet(ArrayList::new);

        trips.add(trip);
        user.setTrips(trips);

        trip.setUser(user);
        userRepository.save(user);
        var savedTrip = tripRepository.save(trip);

        return new TripResponse(savedTrip.getId(), savedTrip.getName(), savedTrip.getDistance(), savedTrip.getTripType(), savedTrip.getTotalTime(), savedTrip.getPointList());
    }

    public TripResponse addPointToTrip(UUID tripId, PointRequest pointRequest) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new EntityNotFoundException("The tour does not exist."));

        Point point = new Point(UUID.randomUUID(), pointRequest.latitude(), pointRequest.longitude(), LocalDateTime.now(), trip);

        point.setTime(pointRequest.time());

        var points = Optional.ofNullable(trip.getPointList()).orElseGet(ArrayList::new);
        points.add(point);
        trip.setPointList(points);

        if (trip.getPointList().size() >= 2) {
            LocalDateTime firstPointTime = points.stream()
                    .map(Point::getTime)
                    .min(LocalDateTime::compareTo)
                    .orElseThrow();

            LocalDateTime lastPointTime = points.stream()
                    .map(Point::getTime)
                    .max(LocalDateTime::compareTo)
                    .orElseThrow();

            trip.setTotalTime(String.valueOf(Duration.between(firstPointTime, lastPointTime).toMillis()));
            trip.setStartTime(firstPointTime);
            trip.setEndTime(lastPointTime);
        } else {
            trip.setTotalTime(null);
        }

        trip.setDistance(trip.getPointList().isEmpty()
                ? 0.0
                : calculateDistance(trip.getPointList()));

        pointRepository.save(point);
        tripRepository.save(trip);

        return new TripResponse(trip.getId(), trip.getName(), trip.getDistance(), trip.getTripType(), trip.getTotalTime(), points);
    }

    public TripResponse importTrip(Jwt jwt, MultipartFile file, TripType tripType) throws Exception {
        List<Point> points = xmlParser.parseFile(file);

        User user = userRepository.findById(UUID.fromString(jwt.getClaim(JwtClaimNames.SUB))).orElseThrow(() -> new UserNotFoundException("User does not exists"));
        Trip trip = Trip.builder()
                .id(UUID.randomUUID())
                .name("---")
                .startTime(null)
                .endTime(null)
                .tripType(tripType)
                .tripStatus(TripStatus.OPEN)
                .pulse(0)
                .rate(null)
                .totalTime(String.valueOf(Duration.between(points.get(0).getTime(), points.get(points.size() - 1).getTime()).toMillis()))
                .calorie(0)
                .strength(0)
                .pointList(points)
                .distance(points.isEmpty() ? 0.0 : calculateDistance(points.stream().sorted(Comparator.comparing(Point::getTime)).toList()))
                .build();

        var trips = Optional.ofNullable(user.getTrips()).orElseGet(ArrayList::new);

        trips.add(trip);
        user.setTrips(trips);
        trip.setUser(user);
        userRepository.save(user);
        var savedTrip = tripRepository.save(trip);

        pointRepository.saveAll(points);

        return new TripResponse(savedTrip.getId(), savedTrip.getName(), savedTrip.getDistance(), savedTrip.getTripType(), savedTrip.getTotalTime(), savedTrip.getPointList());
    }

    public List<List<Double>> generateTripBetweenPoints(SelectedPoints selectedPoints) {

        RestClient restClient = RestClient.create();
        GraphhopperRequest graphhopperRequest = new GraphhopperRequest(List.of(
                selectedPoints.getPoints().get(0),
                selectedPoints.getPoints().get(1)),
                null,
                null,
                List.of("motorway", "ferry", "tunnel"),
                List.of("road_class", "surface"),
                "foot",
                "en",
                true,
                true,
                false);
        ResponseEntity<GraphhopperResponse> res = restClient.post()
                .uri("https://graphhopper.com/api/1/route?key=04490a6e-398d-4ad3-bf09-62947ccdf061")
                .contentType(MediaType.APPLICATION_JSON)
                .body(graphhopperRequest)
                .retrieve()
                .toEntity(GraphhopperResponse.class);

        if (res.getBody() != null) {
            return getPoints(res);
        }

        return new ArrayList<>();
    }

    public List<List<Double>> generateExampleTrip(SelectedPoints selectedPoints) {
        int distance = switch (selectedPoints.getRange()) {
            case 2  -> 7500;
            case 3  -> 15000;
            default -> 3000;
        };

        RestClient restClient = RestClient.create();
        GraphhopperRequest graphhopperRequest = new GraphhopperRequest(List.of(
                selectedPoints.getPoints().get(0),
                randomPoint(selectedPoints.getPoints().get(0), distance),
                selectedPoints.getPoints().get(0)),
                null,
                null,
                List.of("motorway", "ferry", "tunnel"),
                List.of("road_class", "surface"),
                "foot",
                "en",
                true,
                true,
                false);
        ResponseEntity<GraphhopperResponse> res = restClient.post()
                .uri("https://graphhopper.com/api/1/route?key=04490a6e-398d-4ad3-bf09-62947ccdf061")
                .contentType(MediaType.APPLICATION_JSON)
                .body(graphhopperRequest)
                .retrieve()
                .toEntity(GraphhopperResponse.class);

        if (res.getBody() != null) {
            return getPoints(res);
        }
        
        return new ArrayList<>();
    }

    private List<Double> randomPoint(List<Double> start, double radiusMeters) {
        double lng = start.get(0);
        double lat = start.get(1);

        double radius = radiusMeters / 111300.0;

        double u = Math.random();
        double v = Math.random();

        double w = radius * Math.sqrt(u);
        double t = 2 * Math.PI * v;

        double newLng = lng + w * Math.cos(t);
        double newLat = lat + w * Math.sin(t);

        return List.of(newLng, newLat);
    }

    private static List<List<Double>> getPoints(ResponseEntity<GraphhopperResponse> res) {

        return res.getBody()
                .getPaths()
                .get(0)
                .getPoints()
                .getCoordinates()
                .stream()
                .map(p -> List.of(p.get(1), p.get(0)))
                .toList();
    }

    public List<TripResponse> getUserTrips(Jwt jwt) throws UserNotFoundException {
        User user = userRepository.findById(UUID.fromString(jwt.getClaim(JwtClaimNames.SUB))).orElseThrow(() -> new UserNotFoundException("User does not exists"));
        return mapToTripResponseList(user.getTrips());
    }

    private List<TripResponse> mapToTripResponseList(List<Trip> tripList) {
        return tripList
                .stream()
                .map(this::mapToTripResponse
                )
                .toList();
    }

    private TripResponse mapToTripResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getName(),
                trip.getDistance(),
                trip.getTripType(),
                trip.getTotalTime(),
                trip.getPointList().isEmpty() ? new ArrayList<>() : trip.getPointList());
    }

    //TODO to refactor
    public TripResponse addManyPointToTrip(UUID tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new EntityNotFoundException("The tour does not exist."));
        var points = List.of(
//                new Point(UUID.randomUUID(), 50.094444, 21.483333, "TIME", trip),
//                new Point(UUID.randomUUID(), 50.093611, 21.479444, "TIME", trip),
//                new Point(UUID.randomUUID(), 50.091944, 21.476944, "TIME", trip),
//                new Point(UUID.randomUUID(), 50.091667, 21.476944, "TIME", trip),
//                new Point(UUID.randomUUID(), 50.089444, 21.477222, "TIME", trip),
//                new Point(UUID.randomUUID(), 50.089167, 21.477222, "TIME", trip),
                new Point(UUID.randomUUID(), 50.086944, 21.477500, LocalDateTime.parse("2026-02-24T10:37:27.616635"), trip),
                new Point(UUID.randomUUID(), 50.085278, 21.477500, LocalDateTime.parse("2026-02-24T10:38:27.616635"), trip),
                new Point(UUID.randomUUID(), 50.083333, 21.478056, LocalDateTime.parse("2026-02-24T10:39:27.616635"), trip),
                new Point(UUID.randomUUID(), 50.081944, 21.478056, LocalDateTime.parse("2026-02-24T10:40:27.616635"), trip),
                new Point(UUID.randomUUID(), 50.082222, 21.478889, LocalDateTime.parse("2026-02-24T10:41:27.616635"), trip),
                new Point(UUID.randomUUID(), 50.082222, 21.479444, LocalDateTime.parse("2026-02-24T10:42:27.616635"), trip));
//                new Point(UUID.randomUUID(), 50.082222, 21.479722, "TIME", trip),
//                new Point(UUID.randomUUID(), 50.082500, 21.480278, "TIME", trip),
//                new Point(UUID.randomUUID(), 50.083056, 21.481389, "TIME", trip),
//                new Point(UUID.randomUUID(), 50.083611, 21.483333, LocalDateTime.parse("2026-02-24T10:38:27.616635"), trip),
//                new Point(UUID.randomUUID(), 50.084167, 21.483889, LocalDateTime.parse("2026-02-24T10:38:27.616635"), trip),
//                new Point(UUID.randomUUID(), 50.085278, 21.484722, LocalDateTime.parse("2026-02-24T10:38:27.616635"), trip),
//                new Point(UUID.randomUUID(), 50.087778, 21.484722, LocalDateTime.parse("2026-02-24T10:38:27.616635"), trip),
//                new Point(UUID.randomUUID(), 50.088056, 21.485000, LocalDateTime.parse("2026-02-24T10:38:27.616635"), trip),
//                new Point(UUID.randomUUID(), 50.090833, 21.485000, LocalDateTime.parse("2026-02-24T10:38:27.616635"), trip),
//                new Point(UUID.randomUUID(), 50.093889, 21.484444, LocalDateTime.parse("2026-02-24T10:38:27.616635"), trip),
//                new Point(UUID.randomUUID(), 50.094444, 21.483889, LocalDateTime.parse("2026-02-24T10:38:27.616635"), trip),
//                new Point(UUID.randomUUID(), 50.094722, 21.483611, LocalDateTime.parse("2026-02-24T10:38:27.616635"), trip));

        points.forEach(p -> addPointToTrip(tripId, new PointRequest(p.getLatitude(), p.getLongitude(), p.getTime())));

        return new TripResponse(trip.getId(), trip.getName(), trip.getDistance(), trip.getTripType(), trip.getTotalTime(), trip.getPointList());
    }

    private double calculateDistance(List<Point> points) {

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
