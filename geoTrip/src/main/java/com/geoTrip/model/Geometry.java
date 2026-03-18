package com.geoTrip.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class Geometry {

    private String type;
    private List<List<Double>> coordinates;

}