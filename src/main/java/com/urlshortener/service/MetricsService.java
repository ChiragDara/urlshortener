package com.urlshortener.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetricsService {

    private final UrlShortenerService urlShortenerService;

    public MetricsService(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    public Map<String, Integer> topDomains() {
        return urlShortenerService.getDomainCounts().entrySet()
                .stream()
                .sorted((a, b) -> {
                    int cmp = b.getValue().compareTo(a.getValue());
                    if (cmp != 0) return cmp;
                    return a.getKey().compareTo(b.getKey());
                })
                .limit(3)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
