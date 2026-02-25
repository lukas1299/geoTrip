package com.geoTrip.model;
import java.util.ArrayList;

public record TripRequest(
        String name,
         TripType tripType,
         Integer pulse,
         Integer calorie,
         Integer strength,
         ArrayList<Point> points) {
}
