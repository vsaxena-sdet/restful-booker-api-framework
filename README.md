# Restful-Booker API Automation Framework

A structured, framework-level backend API automation project built with
**Java**, **TestNG**, and **REST Assured**. It exercises
[Restful-Booker](https://github.com/mwinteringham/restful-booker)
(`https://restful-booker.herokuapp.com`), an open-source demo API that was
purpose-built by the community for practicing API test automation.

## What's covered

Five tests across the full booking lifecycle:

| # | Test class | Endpoint | What it verifies |
|---|------------|----------|-------------------|
| 1 | `HealthCheckTest` | `GET /ping` | API is up (`201`) |
| 2 | `CreateBookingTest` | `POST /booking` | A new booking is created and the response echoes the submitted fields |
| 3 | `GetBookingTest` | `GET /booking/{id}` | A booking fetched by id matches what was created |
| 4 | `UpdateBookingTest` | `POST /auth` + `PUT /booking/{id}` | An authenticated update persists the new fields |
| 5 | `DeleteBookingTest` | `POST /auth` + `DELETE /booking/{id}` | A booking is removed and a follow-up `GET` returns `404` |

## Project structure

```
pom.xml                        # Maven build, dependencies, surefire -> testng.xml wiring
testng.xml                     # TestNG suite definition (all 5 tests)
src/main/java/com/automation/
  config/ConfigManager.java    # Loads config.properties (base URI, credentials, logging)
  pojo/                        # Booking, BookingDates, BookingResponse, AuthRequest, AuthResponse
  client/BookingApiClient.java # Thin wrapper over REST Assured calls (ping, auth, CRUD)
src/test/java/com/automation/
  base/BaseTest.java           # One-time REST Assured setup (base URI, request spec, logging)
  tests/                       # The 5 test classes listed above
src/test/resources/
  config.properties            # base.uri, auth credentials, log.requests toggle
```

The layering is intentional: **tests -> API client -> POJOs/config**. Tests
only express intent and assertions; request construction and endpoint
details live in `BookingApiClient`, and environment values live in
`config.properties`/`ConfigManager`. This keeps the framework easy to extend
and to point at a different environment without touching test code.

## Prerequisites

- JDK 17 or newer (verify with `java -version`)
- No local Maven installation required — a Maven Wrapper is included.

## Running the tests

```bash
./mvnw clean test
```

(or `mvn clean test` if you have Maven installed globally)

This compiles the project, runs the TestNG suite defined in `testng.xml`
against the live Restful-Booker API, and prints a summary to the console.

## Viewing results

- Console output from Maven/Surefire
- Detailed TestNG HTML/XML reports: `target/surefire-reports/`
- Raw request/response logs for every call are printed to the console when
  `log.requests=true` in `src/test/resources/config.properties`.

## Extending the framework

To add a new test:

1. Add/extend a POJO under `src/main/java/com/automation/pojo` if the new
   endpoint needs one.
2. Add a method to `BookingApiClient` for the new endpoint call.
3. Add a test class under `src/test/java/com/automation/tests` extending
   `BaseTest`, and use the client + POJOs instead of raw REST Assured calls.
4. Register the new class in `testng.xml`.

## Configuration

All environment-specific values live in `src/test/resources/config.properties`:

```properties
base.uri=https://restful-booker.herokuapp.com
auth.username=admin
auth.password=password123
log.requests=true
```

Point `base.uri` at a different deployment of Restful-Booker (e.g. a local
Docker instance) without touching any test code.
