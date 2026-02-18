package com.geoTrip.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.UUID;

@Document(collection = "Trips")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String startTime;
    private String endTime;
    private TripType tripType;
    private TripStatus tripStatus;

    @DBRef
    private User user;

    @DBRef
    private List<Point> pointList;
}
