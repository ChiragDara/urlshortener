package com.urlshortener.controller;

import com.urlshortener.service.UrlShortenerService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class RedirectionController {

    private final UrlShortenerService service;

    public RedirectionController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping("/r/{key}")
    public String redirect(@PathVariable String key) {
        return "redirect:" + service.getOriginalUrl(key);
    }
}