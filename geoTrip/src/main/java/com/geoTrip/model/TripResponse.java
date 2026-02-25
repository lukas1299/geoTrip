package com.geoTrip.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TripResponse(UUID id, String name, Double distance, TripType tripType, String totalTime, List<Point> pointList) {
}
