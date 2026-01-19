package com.urlshortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock
    private UrlShortenerService urlShortenerService;

    @InjectMocks
    private MetricsService metricsService;

    @Test
    void testTopDomains_Empty() {
        when(urlShortenerService.getDomainCounts()).thenReturn(new HashMap<>());

        Map<String, Integer> result = metricsService.topDomains();

        assertTrue(result.isEmpty());
    }

    @Test
    void testTopDomains_LessThanThree() {
        Map<String, Integer> mockData = new HashMap<>();
        mockData.put("example.com", 5);
        mockData.put("google.com", 3);

        when(urlShortenerService.getDomainCounts()).thenReturn(mockData);

        Map<String, Integer> result = metricsService.topDomains();

        assertEquals(2, result.size());
        assertEquals(5, result.get("example.com"));
        assertEquals(3, result.get("google.com"));
    }

    @Test
    void testTopDomains_MoreThanThree() {
        Map<String, Integer> mockData = new HashMap<>();
        mockData.put("example.com", 10);
        mockData.put("google.com", 8);
        mockData.put("github.com", 12);
        mockData.put("stackoverflow.com", 5);
        mockData.put("youtube.com", 3);

        when(urlShortenerService.getDomainCounts()).thenReturn(mockData);

        Map<String, Integer> result = metricsService.topDomains();

        assertEquals(3, result.size());

        // Should be sorted by count descending
        assertEquals("github.com", result.keySet().toArray()[0]);
        assertEquals("example.com", result.keySet().toArray()[1]);
        assertEquals("google.com", result.keySet().toArray()[2]);

        assertEquals(12, result.get("github.com"));
        assertEquals(10, result.get("example.com"));
        assertEquals(8, result.get("google.com"));
    }

    @Test
    void testTopDomains_TieBreaking() {
        Map<String, Integer> mockData = new HashMap<>();
        mockData.put("example.com", 10);
        mockData.put("google.com", 10); // Same count as example.com
        mockData.put("aaa.com", 5);     // Should come after alphabetically

        when(urlShortenerService.getDomainCounts()).thenReturn(mockData);

        Map<String, Integer> result = metricsService.topDomains();

        // With tie, should be sorted alphabetically by domain
        assertEquals("example.com", result.keySet().toArray()[0]); // e before g
        assertEquals("google.com", result.keySet().toArray()[1]);
        assertEquals("aaa.com", result.keySet().toArray()[2]);
    }
}
