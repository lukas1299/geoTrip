package com.geoTrip.model;

import lombok.Data;

import java.util.List;

@Data
public class GraphhopperResponse {

    private Hints hints;
    private Info info;
    private List<Path> paths;

}