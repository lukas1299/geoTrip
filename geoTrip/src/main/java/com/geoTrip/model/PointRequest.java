package com.geoTrip.model;

import java.time.LocalDateTime;

public record PointRequest(double latitude, double longitude, LocalDateTime time) {
}
