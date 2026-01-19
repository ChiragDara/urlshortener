package com.urlshortener.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Model representing a shortened URL.
 * Stores the original URL, shortened key, creation timestamp, and access count.
 * This class serves as the in-memory data structure for URL storage.
 */
@Data
public class ShortUrl {
    private String originalUrl;      // The original long URL
    private String shortKey;         // The shortened key (e.g: "abc123")
    private LocalDateTime createdAt; // When the short URL was created
    private int accessCount;         // How many times this short URL was accessed

    /**
     * Increments the access count by 1.
     * Used when someone clicks on the short URL.
     */
    public void incrementAccessCount() {
        this.accessCount++;
    }
}