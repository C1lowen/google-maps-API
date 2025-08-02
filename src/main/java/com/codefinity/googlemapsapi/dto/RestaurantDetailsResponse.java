package com.codefinity.googlemapsapi.dto;

import com.google.maps.model.OpeningHours;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestaurantDetailsResponse {
    String name;
    double rating;
    String description;
    String address;
    String website;
    String distance;
    OpeningHours opening_hours;
    List<String> photos;
}