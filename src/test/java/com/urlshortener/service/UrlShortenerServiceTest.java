package com.urlshortener.service;

import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.model.ShortUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    private UrlShortenerService urlShortenerService;

    @BeforeEach
    void setUp() {
        urlShortenerService = new UrlShortenerService();
    }

    @Test
    void testCreateShortUrl_ValidUrl() {
        String originalUrl = "https://example.com";

        ShortUrl shortUrl = urlShortenerService.createShortUrl(originalUrl);

        assertNotNull(shortUrl);
        assertEquals(originalUrl, shortUrl.getOriginalUrl());
        assertNotNull(shortUrl.getShortKey());
        assertFalse(shortUrl.getShortKey().isEmpty());
        assertEquals(0, shortUrl.getAccessCount());
    }

    @Test
    void testCreateShortUrl_Idempotency() {
        String originalUrl = "https://example.com";

        ShortUrl firstCall = urlShortenerService.createShortUrl(originalUrl);
        ShortUrl secondCall = urlShortenerService.createShortUrl(originalUrl);

        assertEquals(firstCall.getShortKey(), secondCall.getShortKey());
        assertEquals(firstCall.getOriginalUrl(), secondCall.getOriginalUrl());
    }

    @Test
    void testCreateShortUrl_InvalidUrl() {
        assertThrows(IllegalArgumentException.class, () -> {
            urlShortenerService.createShortUrl("not-a-valid-url");
        });
    }

    @Test
    void testCreateShortUrl_EmptyUrl() {
        assertThrows(IllegalArgumentException.class, () -> {
            urlShortenerService.createShortUrl("");
        });
    }

    @Test
    void testCreateShortUrl_NullUrl() {
        assertThrows(IllegalArgumentException.class, () -> {
            urlShortenerService.createShortUrl(null);
        });
    }

    @Test
    void testCreateShortUrl_ConcurrentIdempotency() throws InterruptedException {
        String originalUrl = "https://concurrent-test.com";

        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        ShortUrl[] results = new ShortUrl[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = urlShortenerService.createShortUrl(originalUrl);
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // All threads should get the same short key
        String firstKey = results[0].getShortKey();
        for (int i = 1; i < threadCount; i++) {
            assertEquals(firstKey, results[i].getShortKey());
        }
    }

    @Test
    void testGetOriginalUrl_ValidKey() {
        String originalUrl = "https://example.com";
        ShortUrl shortUrl = urlShortenerService.createShortUrl(originalUrl);

        String retrieved = urlShortenerService.getOriginalUrl(shortUrl.getShortKey());

        assertEquals(originalUrl, retrieved);
        assertEquals(1, shortUrl.getAccessCount()); // Should be incremented
    }

    @Test
    void testGetOriginalUrl_InvalidKey() {
        assertThrows(UrlNotFoundException.class, () -> {
            urlShortenerService.getOriginalUrl("non-existent-key");
        });
    }

    @Test
    void testGetOriginalUrl_NullKey() {
        assertThrows(UrlNotFoundException.class, () -> {
            urlShortenerService.getOriginalUrl(null);
        });
    }

    @Test
    void testDomainCounting() {
        String[] urls = {
                "https://example.com/page1",
                "https://example.com/page2",
                "https://google.com/search",
                "https://github.com/user",
                "https://example.com/page3"
        };

        for (String url : urls) {
            urlShortenerService.createShortUrl(url);
        }

        Map<String, Integer> domainCounts = urlShortenerService.getDomainCounts();

        assertEquals(3, domainCounts.get("example.com"));
        assertEquals(1, domainCounts.get("google.com"));
        assertEquals(1, domainCounts.get("github.com"));
    }
}