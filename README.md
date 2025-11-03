# Cinema Ticket Pricing API

A RESTful API for calculating cinema ticket prices with support for multiple ticket types, age-based pricing, and bulk discounts.

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **Gradle** (build automation)
- **OpenAPI Generator** (API-first development)
- **Lombok** (reduced boilerplate)
- **JUnit 5 & MockMvc** (testing)


## Prerequisites

- Java 21 or higher
- Gradle 8.x (or use included Gradle wrapper)

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd booking
```

### 2. Build the project

```bash
./gradlew clean build
```

### 3. Run the application
```bash
./gradlew bootRun
```
The application will start on http://localhost:8080

### 4. Access the Swagger UI
```aiexclude
http://localhost:8080/swagger-ui/index.html
```
