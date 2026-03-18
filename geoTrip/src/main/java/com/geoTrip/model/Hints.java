package com.geoTrip.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Hints {

    @JsonProperty("visited_nodes.sum")
    private int visitedNodesSum;

    @JsonProperty("visited_nodes.average")
    private double visitedNodesAverage;

}