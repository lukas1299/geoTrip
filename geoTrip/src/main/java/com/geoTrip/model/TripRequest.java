package com.geoTrip.model;

import java.util.ArrayList;

public record TripRequest(
        String name,
        Double distance,
        TripType tripType,
        Integer pulse,
        Integer calorie,
        String rate,
        Integer strength,
        String totalTime,
        Boolean isFavorite,
        ArrayList<Point> points) {
}
