package com.geoTrip.model;

import java.util.List;
import java.util.UUID;

public record TripResponse(UUID id, String name, List<Point> pointList) {
}
