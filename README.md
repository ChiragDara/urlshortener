# URL Shortener Service

A simple in-memory URL Shortener implemented in Java using Spring Boot.

This service provides:
- Shortening long URLs into Base62 keys
- Redirecting short URLs to original URLs
- Tracking top domains shortened

All data is stored in memory using thread-safe collections.

## Tech Stack

- Java 17
- Spring Boot 3.5.9
- JUnit 5
- Maven
- In-memory storage (ConcurrentHashMap)

## Features

### 1. Shorten URL

Creates a short URL for a given long URL.
If the same long URL is submitted again, the same short key is returned (idempotent behavior).

**Endpoint**: `POST /api/shorten`

**Request Body**:
```json
{
  "originalUrl": "https://example.com/some/long/path"
}
```

**Response**:
```json
{
  "originalUrl": "https://example.com/some/long/path",
  "shortKey": "bM3",
  "shortUrl": "http://localhost:8080/r/bM3"
}
```

2. Redirect to Original URL
   Endpoint: GET /r/{shortKey}

Behavior:

If key exists → HTTP 302 redirect to original URL

If key not found → HTTP 404 with error message

3. Top Domains Metrics
   Returns top 3 domains that have been shortened the most.

Endpoint: GET /api/metrics/top-domains

**Response:**

```json
{
  "google.com": 6,
  "github.com": 4,
  "example.com": 2
}
```

Domains are sorted:
- By count descending
- Alphabetically if counts tie

### URL Validation Rules
- Must not be blank
- Must start with http:// or https://
- Must be a valid URI
- Invalid input returns HTTP 400.

## Running the Application

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

Application starts at: http://localhost:8080

## Running Tests
```bash
mvn test
```

All unit tests are located under: src/test/java
Test coverage includes:
- Base62 encoding
- URL shortening idempotency
- Redirect behavior 
- Exception handling 
- Metrics ranking logic

## Example Curl Commands
### Shorten URL

```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl":"https://google.com"}'
```

### Redirect
```bash
curl -i http://localhost:8080/r/{shortKey}
```

### Metrics
```bash
curl http://localhost:8080/api/metrics/top-domains
```


## Notes
- Storage is in-memory only. Restarting the app clears data.
- Base62 encoding is used to generate compact short keys.
- Thread-safe collections ensure correctness under concurrent requests.
- This project uses Maven for dependency management and building.

## Docker Support
```bash
docker build -t url-shortener .
docker run -p 8080:8080 url-shortener
```


## Project Structure
```text
src/main/java/com/urlshortener/
├── UrlShortenerApplication.java
├── config/
│   └── AppConfig.java
├── controller/
│   ├── MetricsController.java
│   ├── RedirectionController.java
│   └── UrlShortenerController.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── UrlNotFoundException.java
├── model/
│   ├── ShortUrl.java
│   └── UrlRequest.java
├── service/
│   ├── MetricsService.java
│   └── UrlShortenerService.java
└── util/
    └── Base62Util.java
```

## Key Technical Details
- Base62 Encoding: Uses 0-9, a-z, A-Z (62 characters total)
- Thread Safety: Uses ConcurrentHashMap and AtomicLong for thread-safe operations
- Idempotency: Same URL always returns same short key
- Validation: Spring Validation with custom error messages
- Error Handling: Global exception handler with structured error responses

## Chirag Dara - URL Shortener Service Implementation
