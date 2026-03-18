package com.geoTrip.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class GraphhopperRequest {

    private List<List<Double>> points;
    private String algorithm;
    private List<Integer> roundTrip;
    private List<String> snap_preventions;
    private List<String> details;
    private String profile;
    private String locale;
    private boolean instructions;
    private boolean calc_points;
    private boolean points_encoded;
}
