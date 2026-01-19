package com.urlshortener.controller;

import com.urlshortener.config.AppConfig;
import com.urlshortener.model.ShortUrl;
import com.urlshortener.model.UrlRequest;
import com.urlshortener.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UrlShortenerController {

    private final AppConfig appConfig;
    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service,  AppConfig appConfig) {
        this.service = service;
        this.appConfig = appConfig;
    }

    @PostMapping("/shorten")
    public Map<String, String> shorten(@Valid @RequestBody UrlRequest request) {
        ShortUrl shortUrl = service.createShortUrl(request.getOriginalUrl());

        return Map.of(
                "originalUrl", shortUrl.getOriginalUrl(),
                "shortKey", shortUrl.getShortKey(),
                "shortUrl", appConfig.buildShortUrl(shortUrl.getShortKey())
        );
    }
}