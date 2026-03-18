package com.geoTrip.model;

import lombok.Data;

import java.util.List;

@Data
public class Info {

    private List<String> copyrights;
    private int took;
    private String road_data_timestamp;

}