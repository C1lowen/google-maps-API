package com.codefinity.googlemapsapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantResponse {
    private String place_id;
    private String name;
    private double rating;
    private String description;
    private String distance;
    private String photo;
}
