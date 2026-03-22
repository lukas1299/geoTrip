package com.geoTrip.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SelectedPoints {
    List<List<Double>> points;
    int range;
}
