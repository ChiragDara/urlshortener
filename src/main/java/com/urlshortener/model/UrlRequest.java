package com.urlshortener.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * Request model for URL shortening.
 * Contains the original URL that needs to be shortened.
 * Uses validation annotations to ensure the URL is properly formatted.
 */

@Data
public class UrlRequest {
    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(https?://).*", message = "URL must start with http:// or https://")
    private String originalUrl;
}
