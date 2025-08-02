package com.codefinity.googlemapsapi.controller;

import com.codefinity.googlemapsapi.dto.RestaurantDetailsResponse;
import com.codefinity.googlemapsapi.dto.RestaurantResponse;
import com.codefinity.googlemapsapi.dto.RestaurantRequest;
import com.codefinity.googlemapsapi.service.GeoRestaurantsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/places/restaurants")
@AllArgsConstructor
@Log4j2
public class GeoRestaurantsController {

    private GeoRestaurantsService geoRestaurantsService;

    @GetMapping
    public ResponseEntity<?> getNearbyRestaurants(
            @Valid @ModelAttribute RestaurantRequest request
    ) {
        List<RestaurantResponse> nearbyRestaurants = geoRestaurantsService.getNearbyRestaurants(request.getLat(), request.getLng(), request.getRadius());
        return ResponseEntity.status(HttpStatus.OK).body(nearbyRestaurants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurantDetails(
            @PathVariable String id,
            @RequestParam double lat,
            @RequestParam double lng
    ) {
        RestaurantDetailsResponse restaurantDetails = geoRestaurantsService.getRestaurantDetails(id, lat, lng);
        return ResponseEntity.status(HttpStatus.OK).body(restaurantDetails);
    }
}
