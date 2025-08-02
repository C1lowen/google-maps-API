package com.codefinity.googlemapsapi.service;

import com.codefinity.googlemapsapi.dto.RestaurantDetailsResponse;
import com.codefinity.googlemapsapi.dto.RestaurantResponse;
import com.codefinity.googlemapsapi.exception.ExternalServiceException;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class GeoRestaurantsService {

    private final GooglePlacesService googlePlacesService;
    private final PhotoService photoService;

    public List<RestaurantResponse> getNearbyRestaurants(double lat, double lng, int radiusMeters) {
        try {
            PlacesSearchResponse placesResponse = googlePlacesService.findNearbyRestaurants(lat, lng, radiusMeters);
            PlacesSearchResult[] results = placesResponse.results;

            return Arrays.stream(results)
                    .parallel()
                    .map(place -> mapToRestaurantResponse(place, lat, lng))
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch data from Google Places API: {}", e.getMessage());
            throw new ExternalServiceException("Internal error connecting to Google Places API. Please try again later.");
        }
    }

    public RestaurantDetailsResponse getRestaurantDetails(String placeId, double lat, double lng) {
        try {
            PlaceDetails placeDetails = googlePlacesService.getRestaurantDetails(placeId);

            List<String> photoReferences = extractPhotoReferences(placeDetails);
            List<String> photos = photoService.getPhotoUrls(photoReferences);
            double distance = haversine(lat, lng,
                    placeDetails.geometry.location.lat,
                    placeDetails.geometry.location.lng);

            return buildRestaurantDetailsResponse(placeDetails, distance, photos);
        } catch (Exception e) {
            log.error("Failed to fetch restaurant details for placeId {}: {}", placeId, e.getMessage());
            throw new ExternalServiceException("Failed to retrieve restaurant details. Please try again later.");
        }
    }

    private List<String> extractPhotoReferences(PlaceDetails placeDetails) {
        return placeDetails.photos != null
                ? Arrays.stream(placeDetails.photos)
                .map(photo -> photo.photoReference)
                .limit(3)
                .toList()
                : List.of();
    }

    private RestaurantDetailsResponse buildRestaurantDetailsResponse(PlaceDetails placeDetails, double distance, List<String> photos) {
        return RestaurantDetailsResponse.builder()
                .name(placeDetails.name)
                .rating(placeDetails.rating)
                .description(placeDetails.editorialSummary != null
                        ? placeDetails.editorialSummary.overview
                        : "The owner has not left a description")
                .address(placeDetails.formattedAddress)
                .website(placeDetails.website != null ? placeDetails.website.toString() : null)
                .opening_hours(placeDetails.openingHours)
                .distance(formatDistance(distance))
                .photos(photos)
                .build();
    }


    private RestaurantResponse mapToRestaurantResponse(PlacesSearchResult place, double lat, double lng) {
        String imageUrl = null;

        if (place.photos != null && place.photos.length > 0) {
            imageUrl = photoService.getPhotoUrl(place.photos[0].photoReference);
        }

        double distance = haversine(lat, lng,
                place.geometry.location.lat,
                place.geometry.location.lng);

        return RestaurantResponse.builder()
                .place_id(place.placeId)
                .name(place.name)
                .rating(roundTo1Decimal(place.rating))
                .description(place.vicinity)
                .distance(formatDistance(distance))
                .photo(imageUrl)
                .build();
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private String formatDistance(double distanceInKm) {
        if (distanceInKm < 1) {
            int meters = (int) Math.round(distanceInKm * 1000);
            return meters + "м";
        } else {
            return String.format("%.1fкм", distanceInKm);
        }
    }

    private double roundTo1Decimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
