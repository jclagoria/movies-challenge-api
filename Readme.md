# Directa24 Back-End Developer Challenge

### Overview

This project implements a Spring Boot application for the **Directa24 Back-End Developer Challenge**. 
It provides a REST API to fetch movie data from an external API and determine the list of directors with more 
than a specified number of movies directed. The application is built with reactive programming using Spring WebFlux 
and implements efficient caching mechanisms.

### Features
- Fetch and process movie data from an external REST API.
- Return a list of directors who have directed more movies than a given threshold.
- Responses are sorted alphabetically for consistency.
- Error handling with descriptive responses using @ControllerAdvice.
- Efficient data retrieval with caching using Caffeine.
- Fully asynchronous and non-blocking with Spring WebFlux.

### Prerequisites
- **Java Version**: 11
- **Maven Version**: 3.6+
- **Spring Boot Version**: 2.7.15

### Dependencies
The application uses the following dependencies:
- **Spring Boot Starter WebFlux**: For reactive REST API development.
- **Spring Boot Starter Cache**: For caching movie data.
- **Caffeine**: Cache provider for high-performance caching.
- **SpringDoc OpenAPI**: For Swagger UI documentation.
- **Lombok**: To reduce boilerplate code.
- **Reactor Test**: For unit testing reactive streams.
- **Spring Boot Starter Test**: For testing.

### How to Build and Run
1. Clone the Repository:
   ```bash
    git clone https://github.com/jclagoria/movies-challenge-api.git
    cd registration-user-api
   ```
2. Build the Application:
   ```bash
    mvn clean install
   ```
3. Run the Application:
   ```bash
    mvn spring-boot:run
   ```
4. Access the Application: 
   - Swagger UI: http://localhost:8080/swagger-ui.html or http://localhost:8080/webjars/swagger-ui/index.html

### API EndPoints
#### Fetch Directors
**URL**: /api/v1/movies/directors
**METHOD**: GET
**Description**: Fetch directors with movie count exceeding the specified threshold.
**Parameters**:
    - **threshold (required)**: Minimum number of movies a director must have directed.   
#### Example Request:
   ```bash
    curl -X GET "http://localhost:8080/api/v1/movies/directors?threshold=4"
   ```
#### Example Response:
```json
    {
        "directors": ["Martin Scorsese", "Woody Allen"]
    }
```

### Testing the Application
#### Using Swagger UI
1. Start the application.
2. Open http://localhost:8080/swagger-ui.html.
3. Use the GET /api/v1/movies/directors endpoint to test with a threshold parameter.
4. Add circuit breakers for external API failures.


#### Using cURL
Run the following command:
   ```bash
  curl -X GET "http://localhost:8080/api/v1/movies/directors?threshold=4" -H "Accept: application/json"
   ```

### Future Improvements 
#### Code-Level Enhancements
1. Replace Caffeine cache with Redis for distributed caching.
2. Optimize API pagination with parallel fetching.
3. Transition to real-time streaming for incremental processing.

#### Architectural Enhancements
1. Migrate to microservices for better scalability.
2. Enhance observability with metrics and distributed tracing.
      
