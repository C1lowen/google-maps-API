package com.codefinity.googlemapsapi.dto;

import java.time.LocalDateTime;

public record Error(String message,
                       LocalDateTime timestamp,
                       String path) {
    public Error(String message, String path) {
        this(message, LocalDateTime.now(), path);
    }
}
