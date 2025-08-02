package com.codefinity.googlemapsapi.service;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GooglePlacesService {

    private final GeoApiContext context;

    public PlacesSearchResponse findNearbyRestaurants(double lat, double lng, int radius) throws Exception {
        return PlacesApi.nearbySearchQuery(context,
                        new LatLng(lat, lng))
                .radius(radius)
                .type(PlaceType.RESTAURANT)
                .await();
    }

    public PlaceDetails getRestaurantDetails(String placeId) throws Exception {
        return PlacesApi.placeDetails(context, placeId)
                .fields(
                        PlaceDetailsRequest.FieldMask.NAME,
                        PlaceDetailsRequest.FieldMask.RATING,
                        PlaceDetailsRequest.FieldMask.EDITORIAL_SUMMARY,
                        PlaceDetailsRequest.FieldMask.FORMATTED_ADDRESS,
                        PlaceDetailsRequest.FieldMask.WEBSITE,
                        PlaceDetailsRequest.FieldMask.PHOTOS,
                        PlaceDetailsRequest.FieldMask.OPENING_HOURS,
                        PlaceDetailsRequest.FieldMask.GEOMETRY
                )
                .await();
    }
}
