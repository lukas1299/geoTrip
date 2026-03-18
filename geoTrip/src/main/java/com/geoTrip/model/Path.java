package com.geoTrip.model;


import lombok.Data;


import java.util.List;

@Data
public class Path {

    private double distance;
    private double weight;
    private long time;
    private int transfers;

    private List<Object> legs;

    private boolean points_encoded;
    private List<Double> bbox;

    private Geometry points;

    private List<Instruction> instructions;

    private Details details;

    private double ascend;
    private double descend;

    private Geometry snapped_waypoints;

}