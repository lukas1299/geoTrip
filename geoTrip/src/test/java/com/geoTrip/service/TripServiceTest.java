package com.geoTrip.service;

import com.geoTrip.config.MongoTestContainer;
import com.geoTrip.model.Trip;
import com.geoTrip.model.TripResponse;
import com.geoTrip.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TripServiceTest extends MongoTestContainer {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripService tripService;

    @BeforeEach
    void initialTrips() {
        tripRepository.save(new Trip());
    }

    @Test
    void deleteTrip() {
    }

//    @Test
//    void createTrip() {
//
//        TripResponse tripResponse = tripService.createTrip();
//
//    }

    @Test
    void addPointToTrip() {
    }

    @Test
    void importTrip() {
    }

    @Test
    void getUserTrips() {
    }

    @Test
    void addManyPointToTrip() {
    }
}