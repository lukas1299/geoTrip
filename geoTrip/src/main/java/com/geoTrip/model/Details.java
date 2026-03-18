package com.geoTrip.model;

import lombok.Data;

import java.util.List;

@Data
public class Details {

    private List<List<Object>> surface;
    private List<List<Object>> road_class;

}