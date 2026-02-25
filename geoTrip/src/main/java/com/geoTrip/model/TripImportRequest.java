package com.geoTrip.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@RequiredArgsConstructor
public class TripImportRequest {
    MultipartFile file;
    String tripType;
}
