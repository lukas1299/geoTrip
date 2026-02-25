package com.geoTrip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

@Document(collection = "Points")
@AllArgsConstructor
@Getter
@Setter
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private double latitude;
    private double longitude;
    private LocalDateTime time;

    @DBRef
    @JsonIgnore
    private Trip trip;

}
