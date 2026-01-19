package com.urlshortener.service;

import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.ShortUrl;
import com.urlshortener.util.Base62Util;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Core service for URL shortening operations.
 * This service handles:
 * - Generate short keys using a counter + Base62 encoding
 * - Store URL mappings in memory
 * - Ensure idempotency (same URL always returns the same short key)
 * - Resolve short keys back to original URLs
 */
@Service
public class UrlShortenerService {
    // In-memory storage for URLs
    // Using ConcurrentHashMap for thread-safe operations in a multithreaded web environment
    private final ConcurrentHashMap<String, String> longToKey = new ConcurrentHashMap<>();

    // Redirect: shortKey -> ShortUrl
    private final ConcurrentHashMap<String, ShortUrl> keyToShortUrl = new ConcurrentHashMap<>();

    // Counter for Base62 generation
    private final AtomicLong counter = new AtomicLong(0);

    // Metrics: domain -> number of shorten calls
    private final ConcurrentHashMap<String, AtomicInteger> domainCounts = new ConcurrentHashMap<>();


    public Map<String, Integer> getDomainCounts() {
        Map<String, Integer> snapshot = new ConcurrentHashMap<>();
        domainCounts.forEach((k, v) -> snapshot.put(k, v.get()));
        return snapshot;
    }

    /**
     * Creates a shortened URL for the given original URL.
     *
     * @param originalUrl the original URL to shorten
     * @return ShortUrl object containing the original URL and generated short key
     */
    public ShortUrl createShortUrl(String originalUrl) {
        URI uri = validate(originalUrl);
        String normalizedUrl = uri.toString();

        // Metrics
        String domain = uri.getHost().toLowerCase();
        domainCounts.computeIfAbsent(domain, d -> new AtomicInteger()).incrementAndGet();

        // Thread-safe idempotent creation using computeIfAbsent
        String key = longToKey.computeIfAbsent(normalizedUrl, k -> {
            String newKey = Base62Util.encode(counter.incrementAndGet());

            ShortUrl shortUrl = new ShortUrl();
            shortUrl.setOriginalUrl(normalizedUrl);
            shortUrl.setShortKey(newKey);
            shortUrl.setCreatedAt(LocalDateTime.now());
            shortUrl.setAccessCount(0);

            keyToShortUrl.put(newKey, shortUrl);
            return newKey;
        });

        return keyToShortUrl.get(key);

    }

    /**
     * Validates and normalizes the input URL.
     *
     * @param url raw input URL
     * @return parsed and validated URI
     * @throws IllegalArgumentException if validation fails
     */
    private URI validate(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }
        URI uri = URI.create(url.trim());
        if (uri.getScheme() == null || uri.getHost() == null) {
            throw new IllegalArgumentException("URL must include scheme and host");
        }
        return uri;
    }

    /**
     * Retrieves the original URL for a given short key.
     *
     * @param shortKey the short key
     * @return original url
     * @throws UrlNotFoundException if the short key doesn't exist
     */
    public String getOriginalUrl(String shortKey) {
        if (shortKey == null) {
            throw new UrlNotFoundException("Short URL not found: null");
        }
        ShortUrl shortUrl = keyToShortUrl.get(shortKey);
        if (shortUrl == null) {
            throw new UrlNotFoundException("Short URL not found: " + shortKey);
        }
        shortUrl.incrementAccessCount();
        return shortUrl.getOriginalUrl();
    }

}
