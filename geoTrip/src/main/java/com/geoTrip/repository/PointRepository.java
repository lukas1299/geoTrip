package com.geoTrip.repository;

import com.geoTrip.model.Point;
import com.geoTrip.model.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PointRepository extends MongoRepository<Point, UUID> {

    void deleteByTrip(Trip trip);
}
