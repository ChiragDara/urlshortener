package com.urlshortener.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.short-url-prefix:/r/}")
    private String shortUrlPrefix;

    public String buildShortUrl(String key) {
        return baseUrl + shortUrlPrefix + key;
    }
}